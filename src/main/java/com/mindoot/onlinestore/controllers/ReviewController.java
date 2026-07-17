package com.mindoot.onlinestore.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.PendingReviewDto;
import com.mindoot.onlinestore.dto.ReviewPageResponse;
import com.mindoot.onlinestore.dto.ReviewRequest;
import com.mindoot.onlinestore.dto.ReviewResponse;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.service.ReviewService;
import com.mindoot.onlinestore.service.serviceImpl.PendingReviewService;
import com.mindoot.onlinestore.utility.TokenUtils;
import com.mindoot.onlinestore.utility.UserInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	@Autowired
    private final ReviewService reviewService;
	@Autowired
	private final PendingReviewService pendingReviewService;
	/** The token utils. */
	@Autowired
	private TokenUtils tokenUtils;

    @PostMapping("/submit")
    public ResponseEntity<?> submitReview(
            @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String token) {
    	UserInfo userInfo = tokenUtils.getUserInfo(token.substring(7)); // Remove "Bearer "
        reviewService.submitReview(userInfo.getId(), request);
        return ResponseEntity.ok(new MessageResponse("Review submitted successfully",HttpStatus.OK));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                reviewService.getReviewsForProduct(productId)
        );
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<PendingReviewDto>> getPendingReviews(
    		 @RequestHeader("Authorization") String token) {
    	UserInfo userInfo = tokenUtils.getUserInfo(token.substring(7)); // Remove "Bearer "
        return ResponseEntity.ok(
                pendingReviewService.getPendingReviews(userInfo.getId())
        );
    }
    
    @GetMapping("/{variantId}/reviews")
    public ReviewPageResponse getReviews(
            @PathVariable("variantId") Long variantId,

            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "direction", defaultValue = "desc") String direction,
            @RequestParam(name = "rating", required = false) Integer rating
    ) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return reviewService.getReviews(variantId, rating, pageable);
    }
}

