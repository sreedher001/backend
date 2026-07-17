package com.mindoot.onlinestore.service;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.OtpRequest;
import com.mindoot.onlinestore.dto.OtpVerifyRequest;
import com.mindoot.onlinestore.model.User;

@Component
public interface UserService {

	public User getUserProfile(String email);
	
	public User updateUserProfile(String email, User updatedUser);
	
	public void sendOtp(Long userId, OtpRequest request);
	public void verifyOtp(Long userId, OtpVerifyRequest request);
}
