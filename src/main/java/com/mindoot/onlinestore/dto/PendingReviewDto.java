package com.mindoot.onlinestore.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PendingReviewDto {

    private Long orderId;
    private Long variantId;

    private String productName;
    private String variantName;

    private String imageUrl;

    private LocalDateTime deliveredAt;
}
