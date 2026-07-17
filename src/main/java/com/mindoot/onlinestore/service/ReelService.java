package com.mindoot.onlinestore.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.LikeViewCountDto;
import com.mindoot.onlinestore.dto.ReelRequestDto;
import com.mindoot.onlinestore.dto.ReelResponseDto;

@Component
public interface ReelService {

	List<ReelResponseDto> getAllReel(Long userId,String deviceId);

	void incrementViews(Long id);

	public LikeViewCountDto toggleLike(Long reelId, Long userId, String deviceId);

	ReelResponseDto createReel(ReelRequestDto reel);
	
	public ReelResponseDto updateReel(Long id, ReelRequestDto dto);
	
	public void deleteReel(Long id);

	
}
