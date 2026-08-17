package com.mindoot.onlinestore.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.promotiondto.ActiveCouponDto;
import com.mindoot.onlinestore.enums.PromotionType;
import com.mindoot.onlinestore.model.promotion.Promotion;
import com.mindoot.onlinestore.repository.PromotionRepository;

/**
 * Public, unauthenticated promotion endpoints - for marketing display (e.g.
 * the storefront's coupon ticker), as opposed to AdminPromotionController's
 * full CRUD which is gated to ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

	@Autowired
	private PromotionRepository promotionRepository;

	@GetMapping("/active-coupons")
	public List<ActiveCouponDto> getActiveCoupons() {
		LocalDateTime now = LocalDateTime.now();

		return promotionRepository.findByTypeAndActiveTrue(PromotionType.COUPON).stream()
				.filter(promo -> promo.getStartDate() == null || !promo.getStartDate().isAfter(now))
				.filter(promo -> promo.getEndDate() == null || !promo.getEndDate().isBefore(now))
				.filter(promo -> promo.getCouponCode() != null && !promo.getCouponCode().isBlank())
				.map(this::toDto)
				.toList();
	}

	private ActiveCouponDto toDto(Promotion promo) {
		return new ActiveCouponDto(promo.getCouponCode(), promo.getDescription());
	}
}
