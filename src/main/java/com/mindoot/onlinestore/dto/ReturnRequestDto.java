package com.mindoot.onlinestore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnRequestDto {
    private Long orderId;
    private Long variantId;
    private Integer quantity;
    private String reason;
    private String description;
}

