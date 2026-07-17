package com.mindoot.onlinestore.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.PricingContext;
import com.mindoot.onlinestore.enums.ActionType;
import com.mindoot.onlinestore.model.promotion.PromotionAction;

@Component
public interface PromotionActionHandler {

	 ActionType getSupportedAction();

	    double apply(PromotionAction action, PricingContext context);
}
