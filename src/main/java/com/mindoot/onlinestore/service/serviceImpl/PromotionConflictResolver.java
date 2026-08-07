package com.mindoot.onlinestore.service.serviceImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.PricingContext;
import com.mindoot.onlinestore.enums.PromotionGroup;
import com.mindoot.onlinestore.model.promotion.Promotion;
import com.mindoot.onlinestore.model.promotion.PromotionAction;
import com.mindoot.onlinestore.repository.PromotionActionRepository;

@Service
public class PromotionConflictResolver {

    @Autowired
    private PromotionActionRepository actionRepository;

    public List<Promotion> resolve(List<Promotion> promotions,
                                   PricingContext context) {

        Map<Object, List<Promotion>> grouped =
            promotions.stream()
            .collect(Collectors.groupingBy(p ->
                p.getPromotionGroup() != null ? p.getPromotionGroup() : p.getId()));

        List<Promotion> selected = new ArrayList<>();

        for (Object group : grouped.keySet()) {

            List<Promotion> groupPromos = grouped.get(group);

            Promotion best = groupPromos.stream()
                .max(Comparator.comparing(p ->
                    calculateDiscount(p, context)))
                .orElse(null);

            if (best != null) {
                selected.add(best);
            }
        }

        selected.sort(Comparator.comparing(Promotion::getPriority));

        List<Promotion> finalPromotions = new ArrayList<>();

        for (Promotion promo : selected) {

            finalPromotions.add(promo);

            // STACKABLE CONTROL
            if (!promo.isStackable()) {
                break;
            }
        }

        return finalPromotions;
    }

    private double calculateDiscount(Promotion promo,
                                     PricingContext context) {

        PromotionAction action =
            actionRepository.findByPromotionId(promo.getId()).get();

        switch (action.getActionType()) {

            case PERCENT_DISCOUNT:
                return context.getSubtotal() *
                       action.getValue() / 100;

            case FLAT_DISCOUNT:
                return action.getValue();

            default:
                return 0;
        }
    }
}
