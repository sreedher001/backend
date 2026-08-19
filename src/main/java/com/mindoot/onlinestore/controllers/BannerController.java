package com.mindoot.onlinestore.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindoot.onlinestore.dto.BannerUploadDto;
import com.mindoot.onlinestore.model.Banner;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.service.BannerService;
import com.mindoot.onlinestore.utility.TokenUtils;
import com.mindoot.onlinestore.utility.UserInfo;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/api/banners")
public class BannerController {

	@Autowired
	private TokenUtils tokenUtils;
	
	@Autowired
	private BannerService bannerService;
	
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<MessageResponse> uploadBanner(
	        @RequestPart("file") MultipartFile[] files,
	        @RequestPart(value = "mobileFile", required = false) MultipartFile mobileFile,
	        @RequestPart("metadata") String metadataJson,
	        @RequestHeader("Authorization") String token) throws IOException {

	    try {
	        UserInfo userInfo = tokenUtils.getUserInfo(token.substring(7));
	        BannerUploadDto dto = new ObjectMapper().readValue(metadataJson, BannerUploadDto.class);
	        bannerService.uploadBanner(files, mobileFile, dto, userInfo);
	        return ResponseEntity.ok(new MessageResponse("Banner uploaded successfully", HttpStatus.OK));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(new MessageResponse("Upload failed: " + e.getMessage(), HttpStatus.BAD_REQUEST));
	    }
	}
	
	
	@GetMapping("/all-banners")
    public ResponseEntity<List<Banner>> getAllBanners() {
        return ResponseEntity.ok(bannerService.getAllBanners());
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBanner(
            @PathVariable("id") Long id,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "mobileFile", required = false) MultipartFile mobileFile,
            @RequestPart("metadata") String metadataJson) {
        try {
            BannerUploadDto dto = new ObjectMapper().readValue(metadataJson, BannerUploadDto.class);
            Banner updated = bannerService.updateBanner(id, file, mobileFile, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Update failed: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<MessageResponse> deleteBanner(@PathVariable("id") Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(new MessageResponse("Banner deleted successfully", HttpStatus.OK));
    }
}
