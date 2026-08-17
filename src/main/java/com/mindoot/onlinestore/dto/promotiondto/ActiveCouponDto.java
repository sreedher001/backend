package com.mindoot.onlinestore.dto.promotiondto;

/** Minimal public shape for displaying an active coupon (e.g. a storefront ticker). */
public record ActiveCouponDto(String couponCode, String description) {
}
