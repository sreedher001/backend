package com.mindoot.onlinestore.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminProductReviewDto {

	private Long reviewId;

    private Integer rating;
    private String reviewText;

    private Boolean approved;
    private Boolean verifiedPurchase;

    private LocalDateTime createdAt;

    // User
    private Long userId;
    private String username;
    private String userEmail;

    // Product
    private Long productId;
    private String productName;
    private String variantName;

    // Variant
    private Long variantId;
    private String color;
    private String size;
    private Long orderId;
    private String orderNumber;
}
