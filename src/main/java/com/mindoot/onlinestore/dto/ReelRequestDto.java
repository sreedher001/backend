package com.mindoot.onlinestore.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReelRequestDto {

    private String title;
    private String caption;
    private String videoUrl;
    private String thumbnailUrl;
    private Integer durationSeconds;
    private Boolean isActive;

    private Long variantId; //  important (don’t pass full entity)
}
