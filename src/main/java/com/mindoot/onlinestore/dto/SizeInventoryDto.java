package com.mindoot.onlinestore.dto;

import java.time.LocalDateTime;

import com.mindoot.onlinestore.enums.InventoryStatus;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SizeInventoryDto {
    private Long id;
    private String weight;
    private String unit;
    private String variantName;
    private Double price;
    private Double wholesalePrice;
    private String sku;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer lowStockThreshold;
    private InventoryStatus inventoryStatus;
    private LocalDateTime lastUpdated;
}
