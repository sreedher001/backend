package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.model.ProductReview;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.model.VariantRatingSummary;
import com.mindoot.onlinestore.repository.ProductReviewRepository;
import com.mindoot.onlinestore.repository.ProductVariantRepository;
import com.mindoot.onlinestore.repository.VariantRatingSummaryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingAggregationService {

	@Autowired
    private final ProductReviewRepository reviewRepo;
	@Autowired
    private final VariantRatingSummaryRepository summaryRepo;
	
	@Autowired
	private final ProductVariantRepository variantRepo;

    @Transactional
    public void updateRating(Long variantId) {

        List<ProductReview> reviews =
                reviewRepo.findApprovedByProductId(variantId);

        VariantRatingSummary summary =
                summaryRepo.findById(variantId)
                        .orElse(new VariantRatingSummary());

        summary.setVariantId(variantId);
        summary.setTotalReviews(reviews.size());

        summary.setFiveStar(count(reviews, 5));
        summary.setFourStar(count(reviews, 4));
        summary.setThreeStar(count(reviews, 3));
        summary.setTwoStar(count(reviews, 2));
        summary.setOneStar(count(reviews, 1));

        double avg = reviews.stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0);

        summary.setAverageRating(avg);
        summary.setLastUpdated(LocalDateTime.now());

        summaryRepo.save(summary);
        Optional<ProductVariant> variantOptional = variantRepo.findById(variantId);
        ProductVariant productVariant = variantOptional.get();
        productVariant.setRating(avg);
        variantRepo.save(productVariant);
    }

    private int count(List<ProductReview> reviews, int star) {
        return (int) reviews.stream()
                .filter(r -> r.getRating() == star)
                .count();
    }
}

