package com.mindoot.onlinestore.model.promotion;

import org.hibernate.tool.schema.TargetType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "promotion_targets")
@Getter
@Setter
public class PromotionTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long promotionId;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    private Long referenceId;
}