package com.mindoot.onlinestore.dto;

import java.util.List;

import com.mindoot.onlinestore.enums.PurchaseType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDto {
    private Long id;
    private String imageUrl;
    private Long variantId;
    private String variantName;
    private String weight;
    private String unit;
    private Double price;
    private Double originalPrice;
    private Double wholesalePrice;
    private Double discount;
    private String size;
    private Long sizeId;
    private Integer quantity;
    private Integer availableQuantity;
    private List<AvailableSizesDto> availableSizes;
    private Double total;
    private PurchaseType purchaseType;
}
