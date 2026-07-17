package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.PricingContext;
import com.mindoot.onlinestore.dto.promotiondto.AppliedPromotionDto;
import com.mindoot.onlinestore.dto.promotiondto.CouponDto;
import com.mindoot.onlinestore.dto.promotiondto.LockedCouponDto;
import com.mindoot.onlinestore.dto.promotiondto.PromotionActionDto;
import com.mindoot.onlinestore.dto.promotiondto.PromotionActionRequestDto;
import com.mindoot.onlinestore.dto.promotiondto.PromotionConditionDto;
import com.mindoot.onlinestore.dto.promotiondto.PromotionConditionRequestDto;
import com.mindoot.onlinestore.dto.promotiondto.PromotionCreateRequestDto;
import com.mindoot.onlinestore.dto.promotiondto.PromotionDto;
import com.mindoot.onlinestore.dto.promotiondto.PromotionResultDto;
import com.mindoot.onlinestore.enums.ConditionType;
import com.mindoot.onlinestore.enums.DiscountType;
import com.mindoot.onlinestore.enums.PromotionGroup;
import com.mindoot.onlinestore.enums.PromotionType;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.promotion.Promotion;
import com.mindoot.onlinestore.model.promotion.PromotionAction;
import com.mindoot.onlinestore.model.promotion.PromotionCondition;
import com.mindoot.onlinestore.repository.OrderRepository;
import com.mindoot.onlinestore.repository.PromotionActionRepository;
import com.mindoot.onlinestore.repository.PromotionConditionRepository;
import com.mindoot.onlinestore.repository.PromotionRepository;
import com.mindoot.onlinestore.service.PromotionActionHandler;
import com.mindoot.onlinestore.service.PromotionEligibilityService;
import com.mindoot.onlinestore.service.PromotionEngineService;
import com.mindoot.onlinestore.service.ShippingService;

import jakarta.transaction.Transactional;


@Service
public class PromotionEngineServiceImpl implements PromotionEngineService {

	@Autowired
	private PromotionRepository promotionRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private PromotionConditionRepository conditionRepository;

	@Autowired
	private PromotionActionRepository actionRepository;

	@Autowired
	private List<PromotionActionHandler> handlers;

	@Autowired
	private PromotionEligibilityService eligibilityService;

	@Autowired
	private PromotionConflictResolver conflictResolver;

	@Override
	public PromotionResultDto evaluate(PricingContext context, String couponCode, Long userId) {
		double originalSubtotal = context.getSubtotal();
		List<AppliedPromotionDto> appliedPromotions = new ArrayList<>();
		List<Promotion> promotions = promotionRepository.findByActiveTrue();
		List<CouponDto> availableCoupons = new ArrayList<>();
		List<LockedCouponDto> lockedCoupons = new ArrayList<>();
		List<Promotion> eligiblePromotions = promotions.stream().filter(p -> {

			// If coupon is applied -> evaluate only that coupon
			if (couponCode != null) {
				return p.getType() == PromotionType.COUPON && p.getCouponCode().equalsIgnoreCase(couponCode);
			}

			// If no coupon -> run AUTO promotions
			return p.getType() == PromotionType.AUTO;

		}).filter(p -> eligibilityService.isEligible(p, context, couponCode, userId))
				.filter(p -> checkConditions(p, context, userId)).toList();

		List<Promotion> finalPromotions = conflictResolver.resolve(eligiblePromotions, context);

		double discount = 0;

		for (Promotion promo : finalPromotions) {
			double disc = applyAction(promo, context);
			discount += disc;
			appliedPromotions
					.add(new AppliedPromotionDto(promo.getType(), promo.getName(), promo.getDescription(), disc));

		}
		context.setSubtotal(originalSubtotal);

		availableCoupons = getAvailableCoupons(context, userId);
		if (couponCode != null) {
			availableCoupons = availableCoupons.stream().filter(c -> !c.getCouponCode().equalsIgnoreCase(couponCode))
					.toList();
		}
		lockedCoupons = getLockedCoupons(context);

		discount = Math.min(discount, originalSubtotal);

		return new PromotionResultDto(discount, context.getShippingFee(), appliedPromotions, availableCoupons,
				lockedCoupons);
	}

	private boolean isValidPromotion(Promotion promo, String couponCode) {

		// promotion requires coupon
		if (promo.getCouponCode() != null) {

			if (couponCode == null) {
				return false;
			}

			return promo.getCouponCode().equalsIgnoreCase(couponCode);
		}

		return true;
	}

	private boolean checkConditions(Promotion promo, PricingContext context, Long userId) {

		List<PromotionCondition> conditions = conditionRepository.findByPromotionId(promo.getId());

		for (PromotionCondition cond : conditions) {

			switch (cond.getConditionType()) {

			case CART_TOTAL:
				if (context.getSubtotal() < Double.parseDouble(cond.getValue())) {
					return false;
				}
				break;

			case USER_FIRST_ORDER:
				if (!isFirstOrder(userId)) {
					return false;
				}
				break;

			case QUANTITY:
				if (context.getTotalQuantity() < Integer.parseInt(cond.getValue())) {
					return false;
				}
				break;

			default:
				continue;
			}
		}

		return true;
	}

	private boolean isFirstOrder(Long userId) {
		return !orderRepository.existsByUserId(userId);
	}

	private double applyAction(Promotion promo, PricingContext context) {

		PromotionAction action = actionRepository.findByPromotionId(promo.getId()).get();

		PromotionActionHandler handler = handlers.stream().filter(h -> h.getSupportedAction() == action.getActionType())
				.findFirst().orElseThrow();

		double discount = handler.apply(action, context);

		// MAX DISCOUNT CAP
		if (action.getMaxDiscount() != null) {
			discount = Math.min(discount, action.getMaxDiscount());
		}

		return discount;
	}

	@Override
	@Transactional
	public PromotionDto createPromotion(PromotionCreateRequestDto request) {

		Promotion promotion = Promotion.builder().name(request.getName()).description(request.getDescription())
				.couponCode(request.getCouponCode()).type(request.getType()).active(request.isActive())
				.stackable(request.isStackable()).priority(request.getPriority()).usageLimit(request.getUsageLimit())
				.usedCount(0).actionType(request.getActionType()).promotionGroup(request.getGroup())
				.promotionGroup(request.getGroup()).startDate(request.getStartDate()).endDate(request.getEndDate())
				.build();

		Promotion savedPromotion = promotionRepository.save(promotion);

		// Save conditions
		if (request.getConditions() != null) {
			for (PromotionConditionRequestDto cond : request.getConditions()) {

				PromotionCondition condition = PromotionCondition.builder().promotionId(savedPromotion.getId())
						.conditionType(cond.getConditionType()).operator(cond.getOperator()).value(cond.getValue())
						.build();

				conditionRepository.save(condition);
			}
		}

		// Save action
		if (request.getAction() != null) {

			PromotionAction action = PromotionAction.builder().promotionId(savedPromotion.getId())
					.actionType(request.getAction().getActionType()).value(request.getAction().getValue())
					.discountType(request.getAction().getDiscountType())
					.maxDiscount(request.getAction().getMaxDiscount()).build();

			actionRepository.save(action);
		}

		return mapToDto(savedPromotion);
	}

	@Override
	@Transactional
	public PromotionDto updatePromotion(Long id, PromotionCreateRequestDto request) {

		Promotion promotion = promotionRepository.findById(id)
				.orElseThrow(() -> new ApplicationException("Promotion not found", HttpStatus.NOT_FOUND));

		promotion.setName(request.getName());
		promotion.setDescription(request.getDescription());
		promotion.setCouponCode(request.getCouponCode());
		promotion.setType(request.getType());
		promotion.setActive(request.isActive());
		promotion.setStackable(request.isStackable());
		promotion.setPriority(request.getPriority());
		promotion.setUsageLimit(request.getUsageLimit());
		promotion.setStartDate(request.getStartDate());
		promotion.setEndDate(request.getEndDate());
		promotion.setPromotionGroup(request.getGroup());
		promotion.setActionType(request.getActionType());
		if (promotion.getUsageLimit() != null && promotion.getUsageLimit() >= 0) {
			promotion.setUsedCount(promotion.getUsedCount() + 1);

		}
		Promotion savedPromotion = promotionRepository.save(promotion);

		// Remove old conditions
		conditionRepository.deleteByPromotionId(id);

		// Save new conditions
		if (request.getConditions() != null) {

			for (PromotionConditionRequestDto cond : request.getConditions()) {

				PromotionCondition condition = PromotionCondition.builder().promotionId(savedPromotion.getId())
						.conditionType(cond.getConditionType()).operator(cond.getOperator()).value(cond.getValue())
						.build();

				conditionRepository.save(condition);
			}
		}

		// Remove old actions
				actionRepository.deleteByPromotionId(id);
		
		if (request.getAction() != null) {
			PromotionAction action = PromotionAction.builder().promotionId(savedPromotion.getId())
					.actionType(request.getAction().getActionType()).value(request.getAction().getValue())
					.discountType(request.getAction().getDiscountType())
					.maxDiscount(request.getAction().getMaxDiscount()).build();
			actionRepository.save(action);
		}

		return mapToDto(savedPromotion);
	}

	@Override
	public List<PromotionDto> getAllPromotions() {

		List<Promotion> promotions = promotionRepository.findAll();

		return promotions.stream().map(this::mapToDto).toList();
	}

	@Override
	@Transactional
	public void deletePromotion(Long id) {

		Promotion promotion = promotionRepository.findById(id)
				.orElseThrow(() -> new ApplicationException("Promotion not found", HttpStatus.BAD_REQUEST));

		conditionRepository.deleteByPromotionId(id);

		promotionRepository.delete(promotion);
	}

	private PromotionDto mapToDto(Promotion promotion) {

		List<PromotionConditionDto> conditions = conditionRepository.findByPromotionId(promotion.getId()).stream()
				.map(cond -> PromotionConditionDto.builder().conditionType(cond.getConditionType())
						.operator(cond.getOperator()).value(cond.getValue()).build())
				.toList();
		PromotionAction action = actionRepository
		        .findByPromotionId(promotion.getId())
		        .orElse(null);
		PromotionActionDto actionDto = null;

		if (action != null) {
		    actionDto = PromotionActionDto.builder()
		            .actionType(action.getActionType())
		            .discountType(action.getDiscountType())
		            .value(action.getValue())
		            .maxDiscount(action.getMaxDiscount())
		            .build();
		}

		return PromotionDto.builder().id(promotion.getId()).name(promotion.getName())
				.description(promotion.getDescription()).couponCode(promotion.getCouponCode()).type(promotion.getType())
				.active(promotion.isActive()).stackable(promotion.isStackable()).priority(promotion.getPriority())
				.usageLimit(promotion.getUsageLimit()).usedCount(promotion.getUsedCount())
				.startDate(promotion.getStartDate()).endDate(promotion.getEndDate()).conditions(conditions)
				.action(actionDto).promotionGroup(promotion.getPromotionGroup()).build();
	}

	public List<CouponDto> getAvailableCoupons(PricingContext context, Long userId) {

		List<Promotion> coupons = promotionRepository.findByTypeAndActiveTrue(PromotionType.COUPON);

		List<CouponDto> available = coupons.stream()

				.filter(p -> eligibilityService.isEligible(p, context, null, userId))
				.filter(p -> checkConditions(p, context, userId))

				.map(p -> new CouponDto(p.getCouponCode(), p.getDescription(), calculateCouponSavings(p, context),
						p.getPromotionGroup()))

				.toList();

		// pick best coupon per group
		return available.stream().collect(Collectors.groupingBy(CouponDto::getGroup)).values().stream()
				.map(list -> list.stream().max(Comparator.comparing(CouponDto::getPotentialSavings)).orElse(null))
				.toList();
	}

	private double calculateCouponSavings(Promotion promo, PricingContext context) {

		PromotionAction action = actionRepository.findByPromotionId(promo.getId()).get();

		// ⭐ create a safe copy
		PricingContext tempContext = new PricingContext(context.getSubtotal(), context.getShippingFee(),
				context.getTotalQuantity(), context.getItems());

		PromotionActionHandler handler = handlers.stream().filter(h -> h.getSupportedAction() == action.getActionType())
				.findFirst().orElseThrow();

		double discount = handler.apply(action, tempContext);

		if (action.getMaxDiscount() != null) {
			discount = Math.min(discount, action.getMaxDiscount());
		}

		return discount;
	}

	public List<LockedCouponDto> getLockedCoupons(PricingContext context) {

		List<Promotion> coupons = promotionRepository.findByTypeAndActiveTrue(PromotionType.COUPON);
		coupons.stream()
	    .filter(c -> c.getEndDate().isEqual(LocalDateTime.now()))
	    .collect(Collectors.toList());
		List<LockedCouponDto> lockedCoupons = new ArrayList<>();

		for (Promotion promo : coupons) {

			Double requiredValue = getRequiredCartValue(promo);

			if (requiredValue != null && context.getSubtotal() < requiredValue) {

				double remaining = requiredValue - context.getSubtotal();

				lockedCoupons.add(new LockedCouponDto(promo.getCouponCode(), promo.getDescription(),
						"Add ₹" + remaining + " more to unlock"));
			}
		}

		return lockedCoupons;
	}

	private Double getRequiredCartValue(Promotion promo) {

		List<PromotionCondition> conditions = conditionRepository.findByPromotionId(promo.getId());

		for (PromotionCondition cond : conditions) {

			if (cond.getConditionType() == ConditionType.CART_TOTAL) {
				return Double.parseDouble(cond.getValue());
			}
		}

		return null;
	}
}
