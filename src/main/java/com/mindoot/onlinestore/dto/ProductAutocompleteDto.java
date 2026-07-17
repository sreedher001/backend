package com.mindoot.onlinestore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductAutocompleteDto {
    private Long id;
    private String productName;
    private String category;
    private String subCategory;
    private String brand;
    private String variantName;
    private String weight;
    private String unit;
    private String slug;
}
