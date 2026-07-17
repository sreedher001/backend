package com.mindoot.onlinestore.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mindoot.onlinestore.dto.BannerUploadDto;
import com.mindoot.onlinestore.model.Banner;
import com.mindoot.onlinestore.utility.UserInfo;

@Component
public interface BannerService {

	void uploadBanner(MultipartFile[] files, BannerUploadDto dto, UserInfo userInfo);

	List<Banner> getAllBanners();

    Banner updateBanner(Long id, BannerUploadDto dto);

    void deleteBanner(Long id);
	
}
