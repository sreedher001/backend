package com.mindoot.onlinestore.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.mindoot.onlinestore.enums.PurchaseType;

@Data
@Builder
public class OrderDetailResponse {
    private String orderNumber;
    private LocalDateTime orderDate;
    private String paymentMode;
    private ShippingAddressDTO shippingAddress;
    private Double totalAmount;
    private Double subtotal;
    private Double shippingFee;
    private Double cgst;
    private Double sgst;
    private String status;
    private PurchaseType purchaseType;
    private List<OrderItemDto> items;
}
