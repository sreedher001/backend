package com.mindoot.onlinestore.model.promotion;

import java.time.LocalDateTime;

import com.mindoot.onlinestore.enums.ActionType;
import com.mindoot.onlinestore.enums.PromotionGroup;
import com.mindoot.onlinestore.enums.PromotionType;

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
@Table(name = "promotions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String couponCode;
    
    private String description;

    @Enumerated(EnumType.STRING)
    private PromotionType type;
    
    @Enumerated(EnumType.STRING)
    private PromotionGroup promotionGroup;
    
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private String exclusiveGroup;

    private boolean active;

    private boolean stackable;

    private int priority;

    private Integer usageLimit;

    private Integer usedCount;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
