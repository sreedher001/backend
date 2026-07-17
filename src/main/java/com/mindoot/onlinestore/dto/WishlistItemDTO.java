package com.mindoot.onlinestore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistItemDTO {
    private Long variantId;
    private String productName;
    private String imageUrl;
    private Double price;
    private String color;
    private boolean inStock;
}

