package com.mindoot.onlinestore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mindoot.onlinestore.dto.AddToCartRequest;
import com.mindoot.onlinestore.dto.CartResponse;
import com.mindoot.onlinestore.dto.promotiondto.ApplyCouponRequest;
import com.mindoot.onlinestore.enums.PurchaseType;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.service.CartService;
import com.mindoot.onlinestore.utility.TokenUtils;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	@Autowired
	private TokenUtils tokenUtils;

	@PostMapping("/add")
	public ResponseEntity<CartResponse> addToCart(
			@RequestBody AddToCartRequest request,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "guestId", required = false) String guestId
	) {
		Long userId = null;
		if (authorization != null && authorization.startsWith("Bearer ")) {
			userId = tokenUtils.getUserInfo(authorization).getId();
		}

		if (userId == null && guestId == null) {
			throw new ApplicationException("Either Authorization or guestId must be provided", HttpStatus.BAD_REQUEST);
		}

		CartResponse response = cartService.addToCart(userId, guestId, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-cart")
	public ResponseEntity<CartResponse> getCart(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "guestId", required = false) String guestId
	) {
		Long userId = resolveUserId(authorization);

		if (userId == null && guestId == null) {
			throw new ApplicationException("Either Authorization or guestId must be provided", HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.ok(cartService.getUserCart(userId, guestId));
	}

	@PostMapping("/update")
	public ResponseEntity<MessageResponse> updateQuantity(
			@RequestParam("productId") Long productId,
			@RequestParam("quantity") Integer quantity,
			@RequestParam(value = "size", required = false) String size,
			@RequestParam(value = "sizeId", required = false) Long sizeId,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "guestId", required = false) String guestId
	) {
		Long userId = resolveUserId(authorization);

		if (userId == null && guestId == null) {
			throw new ApplicationException("Either Authorization or guestId must be provided", HttpStatus.BAD_REQUEST);
		}
		cartService.updateQuantity(userId, guestId, productId, quantity, size, sizeId);

		return ResponseEntity.ok(new MessageResponse("Cart updated", HttpStatus.OK));
	}

	@PostMapping("/remove")
	public ResponseEntity<MessageResponse> removeItem(
			@RequestParam("productId") Long productId,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "guestId", required = false) String guestId
	) {
		Long userId = resolveUserId(authorization);
		if (userId == null && guestId == null) {
			throw new ApplicationException("Either Authorization or guestId must be provided", HttpStatus.BAD_REQUEST);
		}
		cartService.removeItem(userId, guestId, productId);

		return ResponseEntity.ok(new MessageResponse("Item removed", HttpStatus.OK));
	}

	@PostMapping("/clear")
	public ResponseEntity<MessageResponse> clearCart(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "guestId", required = false) String guestId
	) {
		Long userId = resolveUserId(authorization);
		if (userId == null && guestId == null) {
			throw new ApplicationException("Either Authorization or guestId must be provided", HttpStatus.BAD_REQUEST);
		}
		cartService.clearCart(userId, guestId);

		return ResponseEntity.ok(new MessageResponse("Cart cleared", HttpStatus.OK));
	}

	@PostMapping("/switch-mode")
	public ResponseEntity<CartResponse> switchPurchaseType(
			@RequestParam("type") PurchaseType purchaseType,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "guestId", required = false) String guestId
	) {
		Long userId = resolveUserId(authorization);
		if (userId == null && guestId == null) {
			throw new ApplicationException("Either Authorization or guestId must be provided", HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(cartService.switchPurchaseType(userId, guestId, purchaseType));
	}

	@PostMapping("/apply-coupon")
	public CartResponse applyCoupon(
			@RequestBody ApplyCouponRequest request,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "guestId", required = false) String guestId
	) {
		Long userId = resolveUserId(authorization);
		if (userId == null && guestId == null) {
			throw new ApplicationException("Either Authorization or guestId must be provided", HttpStatus.BAD_REQUEST);
		}
		return cartService.applyCoupon(userId, guestId, request.getCouponCode());
	}

	@PostMapping("/remove-coupon")
	public CartResponse removeCoupon(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "guestId", required = false) String guestId
	) {
		Long userId = resolveUserId(authorization);
		if (userId == null && guestId == null) {
			throw new ApplicationException("Either Authorization or guestId must be provided", HttpStatus.BAD_REQUEST);
		}
		return cartService.removeCoupon(userId, guestId);
	}

	@PostMapping("/merge")
	public ResponseEntity<CartResponse> mergeCart(
			@RequestHeader(value = "guestId") String guestId,
			@RequestHeader("Authorization") String authorization
	) {
		Long userId = tokenUtils.getUserInfo(authorization).getId();
		return ResponseEntity.ok(cartService.mergeCart(guestId, userId));
	}

	private Long resolveUserId(String authorization) {
		if (authorization != null && authorization.startsWith("Bearer ")) {
			return this.tokenUtils.getUserInfo(authorization).getId();
		}
		return null;
	}
}
