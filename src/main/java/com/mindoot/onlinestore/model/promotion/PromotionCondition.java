package com.mindoot.onlinestore.model.promotion;

import com.mindoot.onlinestore.enums.ConditionType;

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
@Table(name = "promotion_conditions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long promotionId;

    @Enumerated(EnumType.STRING)
    private ConditionType conditionType;

    private String operator;

    private String value;
}
