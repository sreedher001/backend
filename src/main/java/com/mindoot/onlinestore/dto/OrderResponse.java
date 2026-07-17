package com.mindoot.onlinestore.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mindoot.onlinestore.enums.OrderStatus;
import com.mindoot.onlinestore.enums.PurchaseType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private String orderNumber;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private String paymentMode;
    private Double subtotal;
    private Double shippingFee;
    private Double cgst;
    private Double sgst;
    private OrderStatus status;
    private PurchaseType purchaseType;
    private String razorPayOrderId;
    private String currency;
    private String name;
    private String email;
    private String phoneNumber;
    private String apikey;
    private Double amount;
    private List<OrderItemDto> items;
}
