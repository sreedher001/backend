package com.mindoot.onlinestore.service;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.ReturnRequestDto;

@Component
public interface ReturnService {

	public void requestReturn(Long userId, ReturnRequestDto dto);
}
