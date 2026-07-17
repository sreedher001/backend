package com.mindoot.onlinestore.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mindoot.onlinestore.enums.OrderStatus;
import com.mindoot.onlinestore.enums.PurchaseType;

import lombok.*;

@Data
@Builder(toBuilder = true)
public class AdminOrderSummaryDto {
    private Long id;
    private String orderNumber;
    private String username;
    private LocalDateTime orderDate;
    private String paymentMode;
    private ShippingAddressDTO shippingAddressDto;
    private Double totalAmount;
    private Double subTotal;
    private Double shippingFee;
    private Double finalTotal;
    private OrderStatus status;
    private PurchaseType purchaseType;
    @Singular("item")
    private List<OrderItemDto> items;
}
