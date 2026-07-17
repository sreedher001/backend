package com.mindoot.onlinestore.dto.promotiondto;

import com.mindoot.onlinestore.enums.ConditionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor   
@AllArgsConstructor
public class PromotionConditionRequestDto {

	private ConditionType conditionType;
	private String operator;
	private String value;
	
}
