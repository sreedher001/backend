package com.mindoot.onlinestore.dto;

import java.util.List;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantResponseDto {
    private Long id;
    private String slug;
    private String variantName;
    private String weight;
    private String unit;
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
    private String imageUrl;
    private Boolean isFeatured;
    private Double rating;
    private Integer totalReviews;
    private List<ProductImageDto> productImage;
    private Integer availableQuantity;
    private String inventoryStatus;
}
