package com.mindoot.onlinestore.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateVariantRequest {
    private Long id;
    private String weight;
    private String unit;
    private String variantName;
    private String sku;
    private String barcode;
    private Double retailPrice;
    private Double mrp;
    private Double wholesalePrice;
    private Boolean retailEnabled;
    private Boolean wholesaleEnabled;
    private Integer minWholesaleQuantity;
    private Double wholesaleDiscount;
    private Boolean active;
    private Integer sortOrder;
    private List<Long> imagesToRemove;
    private Long frontImageId;
    private String frontMarker;
    private Integer availableQuantity;
    private Integer lowStockThreshold;
}
