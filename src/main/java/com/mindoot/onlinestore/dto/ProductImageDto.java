package com.mindoot.onlinestore.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageDto {
    private Long id;
    private String imageUrl;
    private String viewType;
    private Boolean isThumbnail;
    private Integer sortOrder;
}
