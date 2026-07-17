package com.mindoot.onlinestore.service.serviceImpl.promotionhandler;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.PricingContext;
import com.mindoot.onlinestore.model.promotion.Promotion;
import com.mindoot.onlinestore.service.PromotionEligibilityService;

@Service
public class PromotionEligibilityServiceImpl implements PromotionEligibilityService {

	public boolean isEligible(Promotion promo, PricingContext context, String couponCode, Long userId) {

		if (!promo.isActive())
			return false;

		if (promo.getStartDate() != null && promo.getStartDate().isAfter(LocalDateTime.now()))
            return false;

        if (promo.getEndDate() != null && promo.getEndDate().isBefore(LocalDateTime.now()))
            return false;

		if (promo.getUsageLimit() != null && promo.getUsedCount() >  promo.getUsageLimit())
			return false;

		if(couponCode != null) {
		if (promo.getCouponCode() != null && !promo.getCouponCode().equalsIgnoreCase(couponCode))
			return false; 
		}
		return true;
	}
}
