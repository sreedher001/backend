package com.mindoot.onlinestore.service;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.AddToCartRequest;
import com.mindoot.onlinestore.dto.CartResponse;
import com.mindoot.onlinestore.enums.PurchaseType;

@Component
public interface CartService {

    CartResponse addToCart(Long userId, String guestId, AddToCartRequest request);

    CartResponse getUserCart(Long userId, String guestId);

    CartResponse updateQuantity(Long userId, String guestId, Long productId, Integer quantity, String size, Long sizeId);

    CartResponse removeItem(Long userId, String guestId, Long productId);

    void clearCart(Long userId, String guestId);

    CartResponse applyCoupon(Long userId, String guestId, String couponCode);

    CartResponse removeCoupon(Long userId, String guestId);

    CartResponse mergeCart(String guestId, Long userId);

    CartResponse switchPurchaseType(Long userId, String guestId, PurchaseType purchaseType);
}
