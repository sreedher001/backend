package com.mindoot.onlinestore.dto;

import lombok.Data;

@Data
public class BannerUploadDto {
    private String title;
    private String redirectUrl;
    private String bannerType;
}