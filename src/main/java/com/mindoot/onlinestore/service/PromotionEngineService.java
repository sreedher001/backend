package com.mindoot.onlinestore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.PricingContext;
import com.mindoot.onlinestore.dto.promotiondto.PromotionCreateRequestDto;
import com.mindoot.onlinestore.dto.promotiondto.PromotionDto;
import com.mindoot.onlinestore.dto.promotiondto.PromotionResultDto;

@Service
public interface PromotionEngineService {

	public PromotionResultDto evaluate(PricingContext context, String couponCode, Long userId);

	public PromotionDto createPromotion(PromotionCreateRequestDto request);

	public PromotionDto updatePromotion(Long id, PromotionCreateRequestDto request);

	public List<PromotionDto> getAllPromotions();

	public void deletePromotion(Long id);
}
