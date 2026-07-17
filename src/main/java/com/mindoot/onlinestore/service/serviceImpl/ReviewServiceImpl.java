package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.RatingSummaryResponse;
import com.mindoot.onlinestore.dto.ReviewPageResponse;
import com.mindoot.onlinestore.dto.ReviewRequest;
import com.mindoot.onlinestore.dto.ReviewResponse;
import com.mindoot.onlinestore.enums.OrderStatus;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.Order;
import com.mindoot.onlinestore.model.ProductReview;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.model.VariantRatingSummary;
import com.mindoot.onlinestore.repository.OrderRepository;
import com.mindoot.onlinestore.repository.ProductReviewRepository;
import com.mindoot.onlinestore.repository.ProductVariantRepository;
import com.mindoot.onlinestore.repository.UserRepository;
import com.mindoot.onlinestore.repository.VariantRatingSummaryRepository;
import com.mindoot.onlinestore.service.ReviewService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

	@Autowired
    private final ProductReviewRepository reviewRepo;
	@Autowired
    private final ProductVariantRepository variantRepo;
	@Autowired
    private final OrderRepository orderRepo;
	@Autowired
    private final UserRepository userRepo;
    private final RatingAggregationService ratingService;
    @Autowired
    private VariantRatingSummaryRepository summaryRepo;

    @Override
    public void submitReview(Long userId, ReviewRequest request) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ApplicationException("User not found"));

        Order order = orderRepo.findById(request.getOrderId())
                .orElseThrow(() -> new ApplicationException("Order not found"));

        ProductVariant variant = variantRepo.findById(request.getVariantId())
                .orElseThrow(() -> new ApplicationException("Variant not found"));

        //  VALIDATION
        validateEligibility(user, order, variant,request);

        ProductReview review = new ProductReview();
        review.setUser(user);
        review.setOrder(order);
        review.setVariant(variant);
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());
        review.setCreatedAt(LocalDateTime.now());

        reviewRepo.save(review);

        // Update rating summary
        ratingService.updateRating(variant.getId());
    }

    private void validateEligibility(User user, Order order, ProductVariant variant,ReviewRequest request) {

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ApplicationException("Unauthorized review",HttpStatus.BAD_REQUEST);
        }

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new ApplicationException("Order not delivered yet",HttpStatus.BAD_REQUEST);
        }

        boolean variantInOrder = order.getItems().stream()
                .anyMatch(i -> i.getVariant().getId().equals(variant.getId()));

        if (!variantInOrder) {
            throw new ApplicationException("Variant not in order",HttpStatus.BAD_REQUEST);
        }

        if (reviewRepo.existsByUserAndVariant(user, variant)) {
            throw new ApplicationException("You already reviewed this item",HttpStatus.BAD_REQUEST);
        }

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new ApplicationException("Invalid rating",HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public List<ReviewResponse> getReviewsForProduct(Long productId) {

        return reviewRepo.findApprovedByProductId(productId)
                .stream()
                .map(r -> ReviewResponse.builder()
                        .username(r.getUser().getUsername())
                        .rating(r.getRating())
                        .reviewText(r.getReviewText())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();
    }
    
    public ReviewPageResponse getReviews(
            Long variantId,
            Integer rating,
            Pageable pageable) {

        Page<ProductReview> page;

        if (rating != null) {
            page = reviewRepo
                    .findByVariantIdAndApprovedTrueAndRating(
                            variantId, rating, pageable);
        } else {
            page = reviewRepo
                    .findByVariantIdAndApprovedTrue(
                            variantId, pageable);
        }

        VariantRatingSummary summary =
                summaryRepo.findById(variantId)
                        .orElse(new VariantRatingSummary());

        List<ReviewResponse> reviews =
                page.getContent().stream()
                        .map(r -> ReviewResponse.builder()
                                .reviewId(r.getId())
                                .username(
                                    r.getUser().getUsername())
                                .rating(r.getRating())
                                .reviewText(r.getReviewText())
                                
                                .createdAt(r.getCreatedAt())
                                .build())
                        .toList();

        return ReviewPageResponse.builder()
                .summary(mapSummary(summary))
                .reviews(reviews)
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .last(page.isLast())
                .build();
    }

    private RatingSummaryResponse mapSummary(VariantRatingSummary summary) {

        if (summary == null) {
            return RatingSummaryResponse.builder()
                    .averageRating(0.0)
                    .totalReviews(0)
                    .fiveStar(0)
                    .fourStar(0)
                    .threeStar(0)
                    .twoStar(0)
                    .oneStar(0)
                    .build();
        }

        return RatingSummaryResponse.builder()
                .averageRating(summary.getAverageRating() != null 
                        ? summary.getAverageRating() : 0.0)
                .totalReviews(summary.getTotalReviews() != null 
                        ? summary.getTotalReviews() : 0)
                .fiveStar(summary.getFiveStar() != null 
                        ? summary.getFiveStar() : 0)
                .fourStar(summary.getFourStar() != null 
                        ? summary.getFourStar() : 0)
                .threeStar(summary.getThreeStar() != null 
                        ? summary.getThreeStar() : 0)
                .twoStar(summary.getTwoStar() != null 
                        ? summary.getTwoStar() : 0)
                .oneStar(summary.getOneStar() != null 
                        ? summary.getOneStar() : 0)
                .build();
    }

}
