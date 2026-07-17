package com.mindoot.onlinestore.dto;

import java.util.List;

import lombok.Data;

@Data
public class AddVariantRequest {
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
    private Boolean active;
    private Integer sortOrder;
    private Integer availableQuantity;
    private Integer lowStockThreshold;
}
