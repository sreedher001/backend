package com.mindoot.onlinestore.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SnsSmsService {

    public void sendOrderPlacedSms(String phoneNumber, String orderNumber, double totalAmount) {
        log.info("SMS notification (stub): Order {} placed by phone {}, amount: {}", orderNumber, phoneNumber, totalAmount);
    }

    public void sendSms(String phoneNumber, String message) {
        log.info("SMS notification (stub) to {}: {}", phoneNumber, message);
    }

    public void sendOtpSms(String phoneNumber, String otp) {
        log.info("OTP SMS (stub) to {}: {}", phoneNumber, otp);
    }
}
