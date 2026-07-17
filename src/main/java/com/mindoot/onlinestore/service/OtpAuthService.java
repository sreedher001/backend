package com.mindoot.onlinestore.service;

import com.mindoot.onlinestore.model.User;

public interface OtpAuthService {
    void sendOtp(String phoneNumber);
    User verifyOtp(String phoneNumber, String otp);
}
