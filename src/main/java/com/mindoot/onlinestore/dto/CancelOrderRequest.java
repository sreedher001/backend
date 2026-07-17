package com.mindoot.onlinestore.dto;

import lombok.Data;

@Data
public class CancelOrderRequest {
    private String orderNumber;
    private String cancellationReason;
}
