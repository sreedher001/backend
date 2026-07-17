package com.mindoot.onlinestore.dto.promotiondto;

import com.mindoot.onlinestore.enums.ConditionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromotionConditionDto {

    private ConditionType conditionType;

    private String value;
    
    private String operator;

}
