package com.mindoot.onlinestore.service.serviceImpl;

import com.mindoot.onlinestore.model.*;
import com.mindoot.onlinestore.model.promotion.Promotion;
import com.mindoot.onlinestore.repository.*;
import com.mindoot.onlinestore.dto.*;
import com.mindoot.onlinestore.dto.promotiondto.PromotionResultDto;
import com.mindoot.onlinestore.enums.PurchaseType;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.service.CartService;
import com.mindoot.onlinestore.service.PromotionEngineService;
import com.mindoot.onlinestore.service.ShippingService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CartItemRepository cartItemRepository;
	@Autowired
	private ProductVariantRepository productVariantRepository;
	@Autowired
	private ShippingService shippingService;
	@Autowired
	private PromotionEngineService promotionEngineService;
	@Autowired
	private PromotionRepository promotionRepository;

	@Override
	@Transactional
	public CartResponse addToCart(Long userId, String guestId, AddToCartRequest request) {
		ProductVariant variant = productVariantRepository.findById(request.getVariantId())
			.orElseThrow(() -> new ApplicationException("Product variant not found", HttpStatus.NOT_FOUND));

		if (!variant.getActive()) {
			throw new ApplicationException("This variant is not available", HttpStatus.BAD_REQUEST);
		}

		Cart cart = getOrCreateCart(userId, guestId);

		// Enforce purchase type: set cart's purchase type from request, clear if switching
		PurchaseType requestedType = request.getPurchaseType() != null ? request.getPurchaseType() : PurchaseType.RETAIL;
		if (cart.getPurchaseType() != requestedType && cart.getItems() != null && !cart.getItems().isEmpty()) {
			cart.getItems().clear();
			cart.setAppliedCoupon(null);
		}
		cart.setPurchaseType(requestedType);

		// Determine price based on purchase type
		double unitPrice;
		if (requestedType == PurchaseType.WHOLESALE) {
			if (!Boolean.TRUE.equals(variant.getWholesaleEnabled())) {
				throw new ApplicationException("This variant is not available for wholesale", HttpStatus.BAD_REQUEST);
			}
			if (request.getQuantity() != null && variant.getMinWholesaleQuantity() != null
				&& request.getQuantity() < variant.getMinWholesaleQuantity()) {
				throw new ApplicationException(
					"Minimum wholesale quantity is " + variant.getMinWholesaleQuantity(),
					HttpStatus.BAD_REQUEST);
			}
			unitPrice = variant.getWholesalePrice();
		} else {
			unitPrice = variant.getRetailPrice();
		}

		// Check stock
		if (variant.getInventory() == null || variant.getInventory().getAvailableQuantity() < request.getQuantity()) {
			throw new ApplicationException("Insufficient stock", HttpStatus.BAD_REQUEST);
		}

		// Find existing item with same variant in the cart
		Optional<CartItem> existingItem = cart.getItems().stream()
			.filter(item -> item.getVariant() != null && item.getVariant().getId().equals(request.getVariantId()))
			.findFirst();

		if (existingItem.isPresent()) {
			existingItem.get().setQuantity(existingItem.get().getQuantity() + request.getQuantity());
			existingItem.get().setUnitPrice(unitPrice);
			existingItem.get().setTotalPrice(unitPrice * existingItem.get().getQuantity());
		} else {
			CartItem newItem = CartItem.builder()
				.cart(cart)
				.variant(variant)
				.variantName(variant.getVariantName())
				.quantity(request.getQuantity())
				.unitPrice(unitPrice)
				.totalPrice(unitPrice * request.getQuantity())
				.purchaseType(requestedType)
				.build();
			cart.getItems().add(newItem);
		}

		cart.setUpdatedAt(LocalDateTime.now());
		Cart savedCart = cartRepository.save(cart);
		Long effectiveUserId = (userId != null) ? userId : 0L;

		return this.mapToCartResponse(savedCart, effectiveUserId, request.getCouponCode());
	}

	private Cart getOrCreateCart(Long userId, String guestId) {
		if (userId != null) {
			return cartRepository.findByUserId(userId)
				.orElseGet(() -> {
					User user = userRepository.findById(userId).orElseThrow();
					return cartRepository.save(
						Cart.builder()
							.user(user)
							.items(new ArrayList<>())
							.purchaseType(PurchaseType.RETAIL)
							.createdAt(LocalDateTime.now())
							.updatedAt(LocalDateTime.now())
							.build()
					);
				});
		}

		if (guestId != null) {
			return cartRepository.findByGuestId(guestId)
				.orElseGet(() ->
					cartRepository.save(
						Cart.builder()
							.guestId(guestId)
							.items(new ArrayList<>())
							.purchaseType(PurchaseType.RETAIL)
							.createdAt(LocalDateTime.now())
							.updatedAt(LocalDateTime.now())
							.build()
					)
				);
		}

		throw new ApplicationException("No user or guest identity", HttpStatus.NOT_FOUND);
	}

	private CartResponse mapToCartResponse(Cart cart, Long userId, String couponCode) {
		PurchaseType cartType = cart.getPurchaseType() != null ? cart.getPurchaseType() : PurchaseType.RETAIL;

		List<CartItemDto> items = cart.getItems().stream()
			.map(this::mapToCartItemDto)
			.collect(Collectors.toList());

		double subtotal = items.stream()
			.mapToDouble(CartItemDto::getTotal)
			.sum();

		int totalQuantity = cart.getItems().stream()
			.mapToInt(CartItem::getQuantity)
			.sum();

		double shippingFee = shippingService.calculateShippingFee(subtotal);

		// Wholesale: skip coupons/promotions
		double discount = 0;
		List<com.mindoot.onlinestore.dto.promotiondto.AppliedPromotionDto> appliedPromotions = null;
		List<com.mindoot.onlinestore.dto.promotiondto.CouponDto> availableCoupons = null;
		List<com.mindoot.onlinestore.dto.promotiondto.LockedCouponDto> lockedCoupons = null;

		if (cartType == PurchaseType.RETAIL) {
			PricingContext context = new PricingContext(subtotal, shippingFee, totalQuantity, cart.getItems());
			PromotionResultDto promo = promotionEngineService.evaluate(context, couponCode, userId);
			shippingFee = promo.getShippingFee();
			discount = promo.getDiscount();
			appliedPromotions = promo.getAppliedPromotions();
			availableCoupons = promo.getAvailableCoupons();
			lockedCoupons = promo.getLockedCoupons();
		}

		double totalAmount = subtotal + shippingFee - discount;

		return CartResponse.builder()
			.cartId(cart.getId())
			.userId(userId)
			.items(items)
			.subtotal(subtotal)
			.shippingFee(shippingFee)
			.discount(discount)
			.totalAmount(totalAmount)
			.purchaseType(cartType)
			.appliedPromotions(appliedPromotions)
			.availableCoupons(availableCoupons)
			.lockedCoupons(lockedCoupons)
			.build();
	}

	private CartItemDto mapToCartItemDto(CartItem item) {
		ProductVariant variant = item.getVariant();
		String imageUrl = (variant.getImageUrl() != null) ? variant.getImageUrl() :
			(variant.getImages() != null && !variant.getImages().isEmpty()) ?
				variant.getImages().get(0).getImageUrl() : null;

		Integer availableQty = (variant.getInventory() != null) ? variant.getInventory().getAvailableQuantity() : 0;

		return CartItemDto.builder()
			.id(item.getId())
			.variantId(variant.getId())
			.variantName(variant.getVariantName())
			.weight(variant.getWeight())
			.unit(variant.getUnit())
			.price(item.getUnitPrice())
			.originalPrice(variant.getRetailPrice())
			.wholesalePrice(variant.getWholesalePrice())
			.discount(0.0)
			.imageUrl(imageUrl)
			.quantity(item.getQuantity())
			.availableQuantity(availableQty)
			.total(item.getTotalPrice())
			.purchaseType(item.getPurchaseType())
			.build();
	}

	@Override
	@Transactional
	public CartResponse getUserCart(Long userId, String guestId) {
		Cart cart = getOrCreateCart(userId, guestId);
		Long effectiveUserId = (userId != null) ? userId : 0L;
		String coupon = cart.getAppliedCoupon();
		return mapToCartResponse(cart, effectiveUserId, coupon);
	}

	@Override
	@Transactional
	public CartResponse updateQuantity(Long userId, String guestId, Long cartItemId, Integer quantity, String size, Long sizeId) {
		if (quantity == null || quantity < 0) {
			throw new ApplicationException("Quantity must be a positive number", HttpStatus.BAD_REQUEST);
		}

		Cart cart = getOrCreateCart(userId, guestId);

		CartItem item = cart.getItems().stream()
			.filter(i -> i.getId().equals(cartItemId))
			.findFirst()
			.orElseThrow(() -> new ApplicationException("Cart item not found", HttpStatus.NOT_FOUND));

		if (quantity == 0) {
			cart.getItems().remove(item);
		} else {
			ProductVariant variant = item.getVariant();
			double unitPrice = (cart.getPurchaseType() == PurchaseType.WHOLESALE)
				? variant.getWholesalePrice() : variant.getRetailPrice();

			// Wholesale: check minimum quantity
			if (cart.getPurchaseType() == PurchaseType.WHOLESALE
				&& variant.getMinWholesaleQuantity() != null
				&& quantity < variant.getMinWholesaleQuantity()) {
				throw new ApplicationException(
					"Minimum wholesale quantity is " + variant.getMinWholesaleQuantity(),
					HttpStatus.BAD_REQUEST);
			}

			// Check stock
			if (variant.getInventory() == null || variant.getInventory().getAvailableQuantity() < quantity) {
				throw new ApplicationException("Insufficient stock", HttpStatus.BAD_REQUEST);
			}

			item.setQuantity(quantity);
			item.setUnitPrice(unitPrice);
			item.setTotalPrice(unitPrice * quantity);
		}

		cart.setUpdatedAt(LocalDateTime.now());
		Cart updatedCart = cartRepository.save(cart);
		Long effectiveUserId = (userId != null) ? userId : 0L;

		return mapToCartResponse(updatedCart, effectiveUserId, null);
	}

	@Override
	@Transactional
	public CartResponse removeItem(Long userId, String guestId, Long cartItemId) {
		Cart cart = getOrCreateCart(userId, guestId);

		boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));

		if (!removed) {
			throw new ApplicationException("Cart item not found", HttpStatus.NOT_FOUND);
		}
		cart.setAppliedCoupon(null);
		cart.setUpdatedAt(LocalDateTime.now());

		Cart updatedCart = cartRepository.save(cart);
		Long effectiveUserId = (userId != null) ? userId : 0L;

		return mapToCartResponse(updatedCart, effectiveUserId, null);
	}

	@Override
	@Transactional
	public void clearCart(Long userId, String guestId) {
		Cart cart = getOrCreateCart(userId, guestId);
		cart.getItems().clear();
		cart.setAppliedCoupon(null);
		cart.setUpdatedAt(LocalDateTime.now());
		cartRepository.save(cart);
	}

	@Override
	@Transactional
	public CartResponse applyCoupon(Long userId, String guestId, String couponCode) {
		Cart cart = getOrCreateCart(userId, guestId);

		// Coupons not allowed for wholesale orders
		if (cart.getPurchaseType() == PurchaseType.WHOLESALE) {
			throw new ApplicationException("Coupons are not applicable for wholesale orders", HttpStatus.BAD_REQUEST);
		}

		Promotion promo = promotionRepository
			.findByCouponCode(couponCode)
			.orElseThrow(() -> new ApplicationException("Invalid coupon", HttpStatus.BAD_REQUEST));

		cart.setAppliedCoupon(couponCode);
		cart.setUpdatedAt(LocalDateTime.now());

		Cart savedCart = cartRepository.save(cart);
		Long effectiveUserId = (userId != null) ? userId : 0L;

		return mapToCartResponse(savedCart, effectiveUserId, couponCode);
	}

	@Override
	@Transactional
	public CartResponse removeCoupon(Long userId, String guestId) {
		Cart cart = getOrCreateCart(userId, guestId);
		cart.setAppliedCoupon(null);
		cart.setUpdatedAt(LocalDateTime.now());

		Cart updatedCart = cartRepository.save(cart);
		Long effectiveUserId = (userId != null) ? userId : 0L;

		return mapToCartResponse(updatedCart, effectiveUserId, null);
	}

	@Override
	@Transactional
	public CartResponse switchPurchaseType(Long userId, String guestId, PurchaseType purchaseType) {
		Cart cart = getOrCreateCart(userId, guestId);

		// Clear cart when switching modes
		if (cart.getPurchaseType() != purchaseType) {
			cart.getItems().clear();
			cart.setAppliedCoupon(null);
			cart.setPurchaseType(purchaseType);
			cart.setUpdatedAt(LocalDateTime.now());
		}

		Cart savedCart = cartRepository.save(cart);
		Long effectiveUserId = (userId != null) ? userId : 0L;

		return mapToCartResponse(savedCart, effectiveUserId, null);
	}

	@Override
	@Transactional
	public CartResponse mergeCart(String guestId, Long userId) {
		if (guestId == null || guestId.isBlank()) {
			throw new ApplicationException("GuestId is required for merge", HttpStatus.BAD_REQUEST);
		}
		if (userId == null) {
			throw new ApplicationException("UserId is required for merge", HttpStatus.BAD_REQUEST);
		}

		Cart guestCart = cartRepository.findByGuestId(guestId).orElse(null);
		Cart userCart = cartRepository.findByUserId(userId).orElse(null);

		if (guestCart == null || guestCart.getItems() == null || guestCart.getItems().isEmpty()) {
			if (userCart == null) {
				userCart = createNewUserCart(userId);
			}
			return mapToCartResponse(userCart, userId, userCart.getAppliedCoupon());
		}

		if (userCart == null) {
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));

			guestCart.setUser(user);
			guestCart.setGuestId(null);
			guestCart.setUpdatedAt(LocalDateTime.now());

			Cart saved = cartRepository.save(guestCart);
			return mapToCartResponse(saved, userId, saved.getAppliedCoupon());
		}

		// Both carts exist - merge (keep guest cart's purchase type preference)
		userCart.setPurchaseType(guestCart.getPurchaseType());

		Map<Long, CartItem> userItemMap = userCart.getItems().stream()
			.filter(item -> item.getVariant() != null)
			.collect(Collectors.toMap(
				item -> item.getVariant().getId(),
				item -> item,
				(a, b) -> a
			));

		for (CartItem guestItem : guestCart.getItems()) {
			Long vid = guestItem.getVariant() != null ? guestItem.getVariant().getId() : null;
			if (vid != null && userItemMap.containsKey(vid)) {
				CartItem existing = userItemMap.get(vid);
				existing.setQuantity(existing.getQuantity() + guestItem.getQuantity());
				existing.setTotalPrice(existing.getUnitPrice() * existing.getQuantity());
			} else {
				CartItem newItem = CartItem.builder()
					.cart(userCart)
					.variant(guestItem.getVariant())
					.variantName(guestItem.getVariantName())
					.quantity(guestItem.getQuantity())
					.unitPrice(guestItem.getUnitPrice())
					.totalPrice(guestItem.getTotalPrice())
					.purchaseType(guestItem.getPurchaseType())
					.build();
				userCart.getItems().add(newItem);
			}
		}

		if (userCart.getAppliedCoupon() == null && guestCart.getAppliedCoupon() != null) {
			userCart.setAppliedCoupon(guestCart.getAppliedCoupon());
		}

		userCart.setUpdatedAt(LocalDateTime.now());
		guestCart.getItems().clear();
		cartRepository.delete(guestCart);

		Cart saved = cartRepository.save(userCart);
		return mapToCartResponse(saved, userId, saved.getAppliedCoupon());
	}

	private Cart createNewUserCart(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));

		return cartRepository.save(
			Cart.builder()
				.user(user)
				.items(new ArrayList<>())
				.purchaseType(PurchaseType.RETAIL)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build()
		);
	}
}
