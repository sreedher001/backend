package com.mindoot.onlinestore.dto.promotiondto;

import com.mindoot.onlinestore.enums.ActionType;
import com.mindoot.onlinestore.enums.DiscountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionActionRequestDto {


    private ActionType actionType;

    private DiscountType discountType;

    private double value;

    private Double maxDiscount;
}
