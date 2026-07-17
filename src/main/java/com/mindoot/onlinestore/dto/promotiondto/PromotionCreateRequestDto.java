package com.mindoot.onlinestore.dto.promotiondto;

import java.time.LocalDateTime;
import java.util.List;

import com.mindoot.onlinestore.enums.ActionType;
import com.mindoot.onlinestore.enums.PromotionGroup;
import com.mindoot.onlinestore.enums.PromotionType;

import lombok.Data;

@Data
public class PromotionCreateRequestDto {

    private String name;

    private String description;

    private String couponCode;

    private PromotionType type;
    
    private PromotionGroup group;
    
    private ActionType actionType;

    private boolean active;

    private boolean stackable;

    private int priority;

    private Integer usageLimit;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private List<PromotionConditionRequestDto> conditions;

    private PromotionActionRequestDto action;

}
