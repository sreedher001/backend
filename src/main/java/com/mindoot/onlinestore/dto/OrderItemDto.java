package com.mindoot.onlinestore.dto;

import com.mindoot.onlinestore.enums.PurchaseType;

import lombok.*;

@Data
@Builder
public class OrderItemDto {
    private Long productId;
    private Long variantId;
    private String productName;
    private Integer quantity;
    private Double price;
    private String variantName;
    private String weight;
    private String unit;
    private Double total;
    private String imageUrl;
    private Integer discountPercent;
    private PurchaseType purchaseType;
}
