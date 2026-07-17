package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.WishlistItemDTO;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.Product;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.model.WishlistItem;
import com.mindoot.onlinestore.repository.ProductVariantRepository;
import com.mindoot.onlinestore.repository.UserRepository;
import com.mindoot.onlinestore.repository.WishlistItemRepository;
import com.mindoot.onlinestore.service.WishlistService;

import jakarta.transaction.Transactional;

@Service
public class WishlistServiceImpl implements WishlistService {

	@Autowired
	private ProductVariantRepository productVariantRepository;

	@Autowired
	private WishlistItemRepository wishlistItemRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public void addToWishlist(Long id, Long variantId) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new ApplicationException("User not found", HttpStatus.BAD_REQUEST));

		ProductVariant variant = productVariantRepository.findById(variantId)
			.orElseThrow(() -> new ApplicationException("Variant not found", HttpStatus.BAD_REQUEST));

		if (!wishlistItemRepository.existsByUserAndProductVariant(user, variant)) {
			WishlistItem item = WishlistItem.builder()
				.user(user)
				.productVariant(variant)
				.addedAt(LocalDateTime.now())
				.build();
			wishlistItemRepository.save(item);
		}
	}

	@Override
	public List<WishlistItemDTO> getWishlist(Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new ApplicationException("User not found", HttpStatus.BAD_REQUEST));

		List<WishlistItem> wishlistItems = wishlistItemRepository.findByUser(user);

		return wishlistItems.stream().map(item -> {
			ProductVariant variant = item.getProductVariant();
			Product product = variant.getProduct();

			String imageUrl = variant.getImageUrl();
			if (imageUrl == null && variant.getImages() != null && !variant.getImages().isEmpty()) {
				imageUrl = variant.getImages().get(0).getImageUrl();
			}

			Double price = variant.getRetailPrice() != null ? variant.getRetailPrice() : 0.0;
			boolean inStock = variant.getInventory() != null
				&& variant.getInventory().getAvailableQuantity() != null
				&& variant.getInventory().getAvailableQuantity() > 0;

			return WishlistItemDTO.builder()
				.variantId(variant.getId())
				.productName(product.getName())
				.imageUrl(imageUrl)
				.price(price)
				.inStock(inStock)
				.build();
		}).toList();
	}

	@Override
	@Transactional
	public void removeFromWishlist(Long id, Long variantId) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new ApplicationException("User not found", HttpStatus.BAD_REQUEST));
		ProductVariant variant = productVariantRepository.findById(variantId)
			.orElseThrow(() -> new ApplicationException("Variant not found", HttpStatus.BAD_REQUEST));
		wishlistItemRepository.deleteByUserAndProductVariant(user, variant);
	}
}
