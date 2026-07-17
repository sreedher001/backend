package com.mindoot.onlinestore.dto.emaildto;

import java.time.LocalDateTime;
import java.util.List;

import com.mindoot.onlinestore.dto.OrderItemDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderEmailDto {
    private String orderNumber;
    private LocalDateTime orderDate;
    private String userName;
    private String userEmail;
    private String paymentMode;
    private String shippingAddress;
    private double totalAmount;
    private List<OrderItemDto> items;
}

