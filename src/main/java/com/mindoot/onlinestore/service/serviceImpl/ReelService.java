package com.mindoot.onlinestore.service.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.LikeViewCountDto;
import com.mindoot.onlinestore.dto.ProductVariantResponseDto;
import com.mindoot.onlinestore.dto.ReelRequestDto;
import com.mindoot.onlinestore.dto.ReelResponseDto;
import com.mindoot.onlinestore.dto.SizeInventoryDto;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.model.Reel;
import com.mindoot.onlinestore.model.ReelLike;
import com.mindoot.onlinestore.repository.ProductVariantRepository;
import com.mindoot.onlinestore.repository.ReelLikeRepository;
import com.mindoot.onlinestore.repository.ReelRepository;
import com.mindoot.onlinestore.service.ReelService;

@Service
class ReelServiceImpl implements ReelService {

	@Autowired
	ReelRepository reelRepository;
	@Autowired
	ProductVariantRepository productVariantRepository;
	@Autowired
	ReelLikeRepository reelLikeRepository;

	@Override
	public List<ReelResponseDto> getAllReel(Long userId, String deviceId) {
		List<Reel> reels = reelRepository.findAll();
		return reels.stream()
			.map(reel -> mapToReelResponseDto(reel, userId, deviceId))
			.toList();
	}

	private ReelResponseDto mapToReelResponseDto(Reel reel, Long userId, String deviceId) {
		boolean liked;
		if (userId != null) {
			liked = reelLikeRepository.existsByReelIdAndUserId(reel.getId(), userId);
		} else {
			liked = reelLikeRepository.existsByReelIdAndDeviceId(reel.getId(), deviceId);
		}
		return ReelResponseDto.builder()
			.id(reel.getId())
			.title(reel.getTitle())
			.caption(reel.getCaption())
			.videoUrl(reel.getVideoUrl())
			.thumbnailUrl(reel.getThumbnailUrl())
			.durationSeconds(reel.getDurationSeconds())
			.views(reel.getViews())
			.likes(reel.getLikes())
			.liked(liked)
			.productVariant(mapToVariantDto(reel.getVariant()))
			.build();
	}

	private ProductVariantResponseDto mapToVariantDto(ProductVariant variant) {
		Integer availableQty = variant.getInventory() != null ? variant.getInventory().getAvailableQuantity() : 0;
		String inventoryStatus = "OUT_OF_STOCK";
		if (availableQty != null && availableQty > 3) {
			inventoryStatus = "IN_STOCK";
		} else if (availableQty != null && availableQty > 0) {
			inventoryStatus = "LOW_STOCK";
		}

		return ProductVariantResponseDto.builder()
			.id(variant.getId())
			.variantName(variant.getVariantName())
			.weight(variant.getWeight())
			.unit(variant.getUnit())
			.sku(variant.getSku())
			.retailPrice(variant.getRetailPrice())
			.wholesalePrice(variant.getWholesalePrice())
			.wholesaleEnabled(variant.getWholesaleEnabled())
			.minWholesaleQuantity(variant.getMinWholesaleQuantity())
			.wholesaleDiscount(variant.getWholesaleDiscount())
			.active(variant.getActive())
			.sortOrder(variant.getSortOrder())
			.imageUrl(variant.getImageUrl())
			.isFeatured(variant.getIsFeatured())
			.rating(variant.getRating())
			.availableQuantity(availableQty)
			.inventoryStatus(inventoryStatus)
			.build();
	}

	@Override
	public void incrementViews(Long reelId) {
		Reel reel = reelRepository.findById(reelId)
			.orElseThrow(() -> new RuntimeException("Reel not found"));
		reel.setViews(reel.getViews() + 1);
		reelRepository.save(reel);
	}

	public LikeViewCountDto toggleLike(Long reelId, Long userId, String deviceId) {
		Optional<ReelLike> existing;
		if (userId != null) {
			existing = reelLikeRepository.findByReelIdAndUserId(reelId, userId);
		} else {
			existing = reelLikeRepository.findByReelIdAndDeviceId(reelId, deviceId);
		}

		Reel reel = reelRepository.findById(reelId)
			.orElseThrow(() -> new ApplicationException("Reel not found", HttpStatus.OK));
		boolean liked;
		if (existing.isPresent()) {
			reelLikeRepository.delete(existing.get());
			reel.setLikes(reel.getLikes() - 1);
			liked = false;
		} else {
			ReelLike like = new ReelLike();
			like.setReel(reel);
			like.setUserId(userId);
			like.setDeviceId(deviceId);
			reelLikeRepository.save(like);
			reel.setLikes(reel.getLikes() + 1);
			liked = true;
		}

		reelRepository.save(reel);
		return LikeViewCountDto.builder().like(reel.getLikes()).liked(liked).build();
	}

	@Override
	public ReelResponseDto createReel(ReelRequestDto dto) {
		ProductVariant variant = productVariantRepository.findById(dto.getVariantId())
			.orElseThrow(() -> new ApplicationException("Variant not found", HttpStatus.NOT_FOUND));

		Reel reel = Reel.builder()
			.title(dto.getTitle())
			.caption(dto.getCaption())
			.videoUrl(dto.getVideoUrl())
			.thumbnailUrl(dto.getThumbnailUrl())
			.durationSeconds(dto.getDurationSeconds())
			.isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
			.variant(variant)
			.build();

		return mapToReelResponseDto(reelRepository.save(reel), null, null);
	}

	@Override
	public ReelResponseDto updateReel(Long id, ReelRequestDto dto) {
		Reel reel = reelRepository.findById(id)
			.orElseThrow(() -> new ApplicationException("Reel not found", HttpStatus.NOT_FOUND));

		ProductVariant variant = productVariantRepository.findById(dto.getVariantId())
			.orElseThrow(() -> new ApplicationException("Variant not found", HttpStatus.NOT_FOUND));

		reel.setTitle(dto.getTitle());
		reel.setCaption(dto.getCaption());
		reel.setVideoUrl(dto.getVideoUrl());
		reel.setThumbnailUrl(dto.getThumbnailUrl());
		reel.setDurationSeconds(dto.getDurationSeconds());
		reel.setIsActive(dto.getIsActive());
		reel.setVariant(variant);

		return mapToReelResponseDto(reelRepository.save(reel), null, null);
	}

	@Override
	public void deleteReel(Long id) {
		if (!reelRepository.existsById(id)) {
			throw new ApplicationException("Reel not found", HttpStatus.NOT_FOUND);
		}
		reelRepository.deleteById(id);
	}
}
