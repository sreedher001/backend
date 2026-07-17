package com.mindoot.onlinestore.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.WishlistItemDTO;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.service.WishlistService;
import com.mindoot.onlinestore.utility.TokenUtils;
import com.mindoot.onlinestore.utility.UserInfo;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private WishlistService wishlistService;

	@PostMapping("/add/{variantId}")
	public ResponseEntity<MessageResponse> addToWishlist(@PathVariable("variantId") Long variantId,
			@RequestHeader("Authorization") String authorization) {
		UserInfo userInfo = this.tokenUtils.getUserInfo(authorization);
		Long userId = userInfo.getId();

		wishlistService.addToWishlist(userId, variantId);
		return ResponseEntity.ok(new MessageResponse("Added to wishlist", HttpStatus.OK));
	}

	@GetMapping("/get-wishlist")
	public ResponseEntity<List<WishlistItemDTO>> getWishlist(@RequestHeader("Authorization") String authorization) {
		UserInfo userInfo = this.tokenUtils.getUserInfo(authorization);
		Long userId = userInfo.getId();
		List<WishlistItemDTO> wishList = wishlistService.getWishlist(userId);
		return ResponseEntity.ok(wishList);
	}

	@PostMapping("/remove/{variantId}")
	public ResponseEntity<MessageResponse> removeFromWishlist(@PathVariable("variantId") Long variantId,
			@RequestHeader("Authorization") String authorization) {
		UserInfo userInfo = this.tokenUtils.getUserInfo(authorization);
		Long userId = userInfo.getId();

		wishlistService.removeFromWishlist(userId,variantId);
		return ResponseEntity.ok(new MessageResponse("Removed from wishlist", HttpStatus.OK));
	}
}
