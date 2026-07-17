package com.mindoot.onlinestore.dto;

import com.mindoot.onlinestore.enums.PurchaseType;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long variantId;
    private Long sizeId;
    private String color;
    private String couponCode;
    private Integer quantity;
    private PurchaseType purchaseType;
}
