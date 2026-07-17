package com.mindoot.onlinestore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateSizeRequest {
    private Long id;
    private String size;
    private Double price;
    private Double discountPercentage;
    private String sku;
    private String hsnCode;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer lowStockThreshold;
    private Integer stockAdjustment;
}
