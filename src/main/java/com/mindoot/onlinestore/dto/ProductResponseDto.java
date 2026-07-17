package com.mindoot.onlinestore.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String productId;
    private String name;
    private String slug;
    private String shortDescription;
    private String longDescription;
    private Long categoryId;
    private String categoryName;
    private Long subCategoryId;
    private String subCategoryName;
    private String brand;
    private String sku;
    private String barcode;
    private Boolean active;
    private Boolean isFeatured;
    private String thumbnail;
    private String seoTitle;
    private String seoDescription;
    private String tags;
    private Integer sortOrder;
    private Double rating;
    private LocalDateTime uploadedAt;
    private ProductVariantResponseDto variant;
    private List<ProductVariantResponseDto> variants;
    private List<ProductImageDto> productImages;
}
