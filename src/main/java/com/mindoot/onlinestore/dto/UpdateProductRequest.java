package com.mindoot.onlinestore.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {
    private String name;
    private String slug;
    private String shortDescription;
    private String longDescription;
    private Long categoryId;
    private Long subCategoryId;
    private String brand;
    private String sku;
    private String barcode;
    private Boolean active;
    private Boolean isFeatured;
    private String seoTitle;
    private String seoDescription;
    private String tags;
    private Integer sortOrder;
}
