package com.mindoot.onlinestore.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mindoot.onlinestore.enums.PurchaseType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderSummaryDto {
    private Long id;
    private String orderNumber;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private String paymentMode;
    private String status;
    private PurchaseType purchaseType;
    private List<OrderItemDto> items;
}
