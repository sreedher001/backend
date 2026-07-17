package com.mindoot.onlinestore.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String name;
    private String productId;
    private String slug;
    private String category;
    private String subCategory;
    private String brand;
    private String imageUrl;
    private Double rating;
    private Boolean isFeatured;
    private Boolean wholesaleEnabled;
}
