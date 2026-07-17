package com.mindoot.onlinestore.dto;

import java.time.LocalDateTime;

import com.mindoot.onlinestore.enums.ReturnStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminReturnListDto {

    private Long returnId;
    private Long orderId;
    private String orderNumber;
    private String productName;
    private String variantInfo; // Color / Size
    private String reason;
    private ReturnStatus status;
    private LocalDateTime requestedAt;
    private String customerName;
}

