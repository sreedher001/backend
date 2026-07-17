package com.mindoot.onlinestore.service;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.PricingContext;
import com.mindoot.onlinestore.model.promotion.Promotion;

@Component
public interface PromotionEligibilityService {

	public boolean isEligible(Promotion promo,
            PricingContext context,
            String couponCode,
            Long userId);
}
