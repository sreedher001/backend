package com.mindoot.onlinestore.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@Builder
public class ReviewResponse {
	private Long reviewId;
    private String username;
    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;
}

