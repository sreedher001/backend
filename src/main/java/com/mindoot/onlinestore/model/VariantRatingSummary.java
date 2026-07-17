package com.mindoot.onlinestore.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "variant_rating_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VariantRatingSummary {

    @Id
    private Long variantId;

    private Double averageRating;
    private Integer totalReviews;

    private Integer fiveStar;
    private Integer fourStar;
    private Integer threeStar;
    private Integer twoStar;
    private Integer oneStar;

    private LocalDateTime lastUpdated;
}

