package com.mindoot.onlinestore.dto.promotiondto;

import java.time.LocalDateTime;
import java.util.List;

import com.mindoot.onlinestore.enums.PromotionGroup;
import com.mindoot.onlinestore.enums.PromotionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromotionDto {

    private Long id;

    private String name;

    private String description;

    private PromotionType type;
    private String couponCode;

    private boolean active;
    
    private boolean stackable;
    
    private int priority;

    private Integer usageLimit;

    private Integer usedCount;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private List<PromotionConditionDto> conditions;

    private PromotionActionDto action;
    
    private PromotionGroup promotionGroup;

}
