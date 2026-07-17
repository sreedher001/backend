package com.mindoot.onlinestore.dto;

import com.mindoot.onlinestore.enums.PurchaseType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {
    private Long shippingAddressId;
    private String paymentMode;
    private PurchaseType purchaseType;
}
