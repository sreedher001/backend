package com.mindoot.onlinestore.dto;

import java.util.List;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductWithVariantsRequest {
    private String name;
    private String shortDescription;
    private String longDescription;
    private String brand;
    private String sku;
    private String barcode;
    private String tags;
    private Boolean isFeatured;
    private Long categoryId;
    private Long subCategoryId;
    private String seoTitle;
    private String seoDescription;
    private List<VariantDto> variants;
}
