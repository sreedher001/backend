package com.mindoot.onlinestore.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mindoot.onlinestore.dto.BannerUploadDto;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.Banner;
import com.mindoot.onlinestore.repository.BannerRepository;
import com.mindoot.onlinestore.utility.UserInfo;

@Service
public class BannerServiceImpl implements BannerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BannerServiceImpl.class);

	@Autowired
	private LocalFileStorageService fileStorageService;

	@Autowired
	private BannerRepository bannerRepository;

	@Override
	public void uploadBanner(MultipartFile[] files, MultipartFile mobileFile, BannerUploadDto bannerUploadDto, UserInfo userInfo) {
		try {
			List<String> uploadedUrls = fileStorageService.uploadImages(files, "banners");

			// A separate mobile crop only makes sense when this request is
			// creating a single banner - a batch of unrelated desktop images
			// can't share one mobile image.
			String mobileImageUrl = (mobileFile != null && !mobileFile.isEmpty() && uploadedUrls.size() == 1)
					? fileStorageService.uploadImage(mobileFile, "banners")
					: null;

			for (String imageUrl : uploadedUrls) {
				Banner banner = new Banner();
				banner.setImageUrl(imageUrl);
				banner.setMobileImageUrl(mobileImageUrl);
				banner.setTitle(bannerUploadDto.getTitle());
				banner.setRedirectUrl(bannerUploadDto.getRedirectUrl());
				banner.setBannerType(bannerUploadDto.getBannerType());
				banner.setPurchaseType(bannerUploadDto.getPurchaseType() != null ? bannerUploadDto.getPurchaseType() : "BOTH");
				banner.setUploadedAt(LocalDateTime.now());
				banner.setUploadedBy(userInfo.getId());
				bannerRepository.save(banner);
			}

			LOGGER.info("Banner upload successful. {} images saved.", uploadedUrls.size());

		} catch (Exception e) {
			LOGGER.error("Failed to upload banner: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to upload banner: " + e.getMessage());
		}
	}

	@Override
	public List<Banner> getAllBanners() {
		return bannerRepository.findAllByOrderByUploadedAtDesc();
	}

	@Override
	public Banner updateBanner(Long id, MultipartFile file, MultipartFile mobileFile, BannerUploadDto dto) {
		Banner existingBanner = bannerRepository.findById(id)
			.orElseThrow(() -> new ApplicationException("Banner not found with ID: " + id, HttpStatus.NOT_FOUND));

		try {
			if (file != null && !file.isEmpty()) {
				String oldImageUrl = existingBanner.getImageUrl();
				existingBanner.setImageUrl(fileStorageService.uploadImage(file, "banners"));
				if (oldImageUrl != null) {
					fileStorageService.deleteFile(oldImageUrl);
				}
			}
			if (mobileFile != null && !mobileFile.isEmpty()) {
				String oldMobileImageUrl = existingBanner.getMobileImageUrl();
				existingBanner.setMobileImageUrl(fileStorageService.uploadImage(mobileFile, "banners"));
				if (oldMobileImageUrl != null) {
					fileStorageService.deleteFile(oldMobileImageUrl);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Failed to update banner image: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to update banner image: " + e.getMessage());
		}

		existingBanner.setTitle(dto.getTitle());
		existingBanner.setRedirectUrl(dto.getRedirectUrl());
		existingBanner.setBannerType(dto.getBannerType());
		existingBanner.setPurchaseType(dto.getPurchaseType() != null ? dto.getPurchaseType() : "BOTH");
		existingBanner.setUploadedAt(LocalDateTime.now());

		return bannerRepository.save(existingBanner);
	}

	@Override
	public void deleteBanner(Long id) {
		Banner banner = bannerRepository.findById(id)
			.orElseThrow(() -> new ApplicationException("Banner not found with ID: " + id, HttpStatus.NOT_FOUND));

		if (banner.getImageUrl() != null) {
			fileStorageService.deleteFile(banner.getImageUrl());
		}
		if (banner.getMobileImageUrl() != null) {
			fileStorageService.deleteFile(banner.getMobileImageUrl());
		}

		bannerRepository.delete(banner);
	}
}
