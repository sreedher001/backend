package com.mindoot.onlinestore.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mindoot.onlinestore.dto.ReviewPageResponse;
import com.mindoot.onlinestore.dto.ReviewRequest;
import com.mindoot.onlinestore.dto.ReviewResponse;

public interface ReviewService {

    void submitReview(Long userId, ReviewRequest request);

    List<ReviewResponse> getReviewsForProduct(Long productId);

	ReviewPageResponse getReviews(Long variantId, Integer rating, Pageable pageable);
}