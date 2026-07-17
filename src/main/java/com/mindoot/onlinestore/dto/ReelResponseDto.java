package com.mindoot.onlinestore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReelResponseDto {

    private Long id;

    private String title;

    private String caption;

    private String videoUrl;

    private String thumbnailUrl;

    private Integer durationSeconds;

    private Long views;

    private Long likes;
    
    private Boolean liked;
    
    private ProductVariantResponseDto productVariant;
}
