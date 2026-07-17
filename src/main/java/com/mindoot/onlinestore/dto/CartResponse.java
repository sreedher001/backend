package com.mindoot.onlinestore.dto;

import java.util.List;

import com.mindoot.onlinestore.dto.promotiondto.AppliedPromotionDto;
import com.mindoot.onlinestore.dto.promotiondto.CouponDto;
import com.mindoot.onlinestore.dto.promotiondto.LockedCouponDto;
import com.mindoot.onlinestore.enums.PurchaseType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponse {
    private Long cartId;
    private Long userId;
    private double totalAmount;
    private Double shippingFee;
    private Double subtotal;
    private Double discount;
    private Double cgst;
    private Double sgst;
    private List<CartItemDto> items;
    private List<AppliedPromotionDto> appliedPromotions;
    private List<CouponDto> availableCoupons;
    private List<LockedCouponDto> lockedCoupons;
    private PurchaseType purchaseType;
}
