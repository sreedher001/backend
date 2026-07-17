package com.mindoot.onlinestore.service.serviceImpl.promotionhandler;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.PricingContext;
import com.mindoot.onlinestore.enums.ActionType;
import com.mindoot.onlinestore.model.promotion.PromotionAction;
import com.mindoot.onlinestore.service.PromotionActionHandler;

@Component
public class FlatDiscountHandler implements PromotionActionHandler {

    @Override
    public ActionType getSupportedAction() {
        return ActionType.FLAT_DISCOUNT;
    }

    @Override
    public double apply(PromotionAction action, PricingContext context) {
        return action.getValue();
    }
}