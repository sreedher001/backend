package com.mindoot.onlinestore.dto.promotiondto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PromotionResultDto {

    private double discount;

    private double shippingFee;
    
    private List<AppliedPromotionDto> appliedPromotions;
    
    private List<CouponDto> availableCoupons;
    
    private List<LockedCouponDto> lockedCoupons;

}
