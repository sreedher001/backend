package com.mindoot.onlinestore.dto.promotiondto;

import com.mindoot.onlinestore.enums.PromotionGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponDto {

	private String couponCode;
	private String description;
	private double potentialSavings;
    private PromotionGroup group;
}
