package com.mindoot.onlinestore.dto;

import com.mindoot.onlinestore.enums.InventoryStatus;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SizeDto {
    private String size;
    private Double price;
    private Integer quantity;
    private Double discountPercentage;
    private String sku;
    private String hsnCode;
    private InventoryStatus inventoryStatus;
}
