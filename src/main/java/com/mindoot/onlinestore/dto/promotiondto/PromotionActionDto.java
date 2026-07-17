package com.mindoot.onlinestore.dto.promotiondto;

import com.mindoot.onlinestore.enums.ActionType;
import com.mindoot.onlinestore.enums.DiscountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionActionDto {

    private ActionType actionType;

    private Double value;

    private DiscountType discountType;

    private Double maxDiscount;
}