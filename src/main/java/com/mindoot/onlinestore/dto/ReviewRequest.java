package com.mindoot.onlinestore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    private Long orderId;
    private Long variantId;
    private Integer rating; // 1–5
    private String reviewText;
}

