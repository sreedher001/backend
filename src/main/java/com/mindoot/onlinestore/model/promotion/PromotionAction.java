package com.mindoot.onlinestore.model.promotion;

import com.mindoot.onlinestore.enums.ActionType;
import com.mindoot.onlinestore.enums.DiscountType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "promotion_actions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long promotionId;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private double value;

    private Double maxDiscount;
}
