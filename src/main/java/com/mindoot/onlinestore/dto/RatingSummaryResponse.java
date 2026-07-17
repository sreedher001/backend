package com.mindoot.onlinestore.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingSummaryResponse {

    private Double averageRating;
    private Integer totalReviews;

    private Integer fiveStar;
    private Integer fourStar;
    private Integer threeStar;
    private Integer twoStar;
    private Integer oneStar;
}
