package com.mindoot.onlinestore.dto;

import java.util.List;

import com.mindoot.onlinestore.enums.PurchaseType;

import lombok.Data;

@Data
public class ProductFilterDto {
    private Long categoryId;
    private Long subCategoryId;
    private String brand;
    private Double minPrice;
    private Double maxPrice;
    private Boolean inStock;
    private Boolean isFeatured;
    private PurchaseType purchaseType;
    private List<String> tags;
    private String query;
}
