package com.mindoot.onlinestore.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadImageDto {
    private String name;
    private String slug;
    private String shortDescription;
    private String longDescription;
    private Long categoryId;
    private Long subCategoryId;
    private String brand;
    private String sku;
    private String barcode;
    private Boolean isFeatured;
    private Boolean active;
    private String seoTitle;
    private String seoDescription;
    private String tags;
    private Integer sortOrder;
    private String weight;
    private String unit;
    private String variantName;
    private Double retailPrice;
    private Double wholesalePrice;
    private Boolean wholesaleEnabled;
    private Integer minWholesaleQuantity;
    private Double wholesaleDiscount;
    private Integer availableQuantity;
    private Integer lowStockThreshold;
}
