package com.mindoot.onlinestore.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.AdminProductReviewDto;

@Component
public interface AdminReviewService {

	Page<AdminProductReviewDto> getAllReviews(Pageable pageable);
}
