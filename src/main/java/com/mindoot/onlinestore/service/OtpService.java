package com.mindoot.onlinestore.service;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.model.User;


public interface OtpService {

	public String generateOtp(User user);
	public boolean verifyOtp(User user, String otp);
}
