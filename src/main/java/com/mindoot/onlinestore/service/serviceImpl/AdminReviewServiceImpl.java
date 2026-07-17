package com.mindoot.onlinestore.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.AdminProductReviewDto;
import com.mindoot.onlinestore.model.Product;
import com.mindoot.onlinestore.model.ProductReview;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.repository.ProductReviewRepository;
import com.mindoot.onlinestore.service.AdminReviewService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminReviewServiceImpl implements AdminReviewService {

	@Autowired
	private final ProductReviewRepository reviewRepository;

	public Page<AdminProductReviewDto> getAllReviews(Pageable pageable) {
		return reviewRepository.findAll(pageable)
			.map(this::mapToDto);
	}

	private AdminProductReviewDto mapToDto(ProductReview review) {
		ProductVariant variant = review.getVariant();
		Product product = variant.getProduct();

		return AdminProductReviewDto.builder()
			.reviewId(review.getId())
			.rating(review.getRating())
			.reviewText(review.getReviewText())
			.approved(review.getApproved())
			.verifiedPurchase(review.getVerifiedPurchase())
			.createdAt(review.getCreatedAt())
			.userId(review.getUser().getId())
			.username(review.getUser().getUsername())
			.userEmail(review.getUser().getEmail())
			.productId(product.getId())
			.productName(product.getName())
			.variantName(variant.getVariantName())
			.variantId(variant.getId())
			.orderId(review.getOrder().getId())
			.orderNumber(review.getOrder().getOrderNumber())
			.build();
	}
}
