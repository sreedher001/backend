package com.mindoot.onlinestore.service.serviceImpl.promotionhandler;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.PricingContext;
import com.mindoot.onlinestore.enums.ActionType;
import com.mindoot.onlinestore.model.promotion.PromotionAction;
import com.mindoot.onlinestore.service.PromotionActionHandler;

@Component
public class PercentDiscountHandler implements PromotionActionHandler {

    @Override
    public ActionType getSupportedAction() {
        return ActionType.PERCENT_DISCOUNT;
    }

    @Override
    public double apply(PromotionAction action, PricingContext context) {
        return context.getSubtotal() * action.getValue() / 100;
    }
}
