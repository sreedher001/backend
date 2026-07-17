package com.mindoot.onlinestore.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminStockInterestResponse {

    private Long variantId;
    private String variantName;
    private Long waitingCount;
    private LocalDateTime oldestRequest;
}
