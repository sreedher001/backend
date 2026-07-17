package com.mindoot.onlinestore.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.WishlistItemDTO;

@Component
public interface WishlistService {

	public void addToWishlist(Long userId, Long variantId);

	public List<WishlistItemDTO> getWishlist(Long userId);

	public void removeFromWishlist(Long userId, Long variantId);
}
