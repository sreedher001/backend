package com.mindoot.onlinestore.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariantDto {
    private Long id;
    private String weight;
    private String unit;
    private String variantName;
    private String sku;
    private String barcode;
    private Double retailPrice;
    private Double wholesalePrice;
    private Boolean wholesaleEnabled;
    private Integer minWholesaleQuantity;
    private Double wholesaleDiscount;
    private Integer availableQuantity;
    private Integer lowStockThreshold;
    private Boolean active;
    private Integer sortOrder;
}
