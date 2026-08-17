package com.mindoot.onlinestore.service;

import com.mindoot.onlinestore.exception.ApplicationException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.from-number}")
    private String fromNumber;

    @PostConstruct
    private void initTwilio() {
        if (!accountSid.isBlank() && !authToken.isBlank()) {
            Twilio.init(accountSid, authToken);
        }
    }

    public void sendOrderPlacedSms(String phoneNumber, String orderNumber, double totalAmount) {
        sendSms(phoneNumber, "Your order " + orderNumber + " has been placed. Total: " + totalAmount);
    }

    public void sendSms(String phoneNumber, String message) {
        if (accountSid.isBlank() || authToken.isBlank() || fromNumber.isBlank()) {
            log.warn("Twilio is not configured (twilio.account-sid/auth-token/from-number); SMS to {} was not sent", phoneNumber);
            throw new ApplicationException("SMS sending is not configured", HttpStatus.SERVICE_UNAVAILABLE);
        }

        try {
            Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(fromNumber), message).create();
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
            throw new ApplicationException("Unable to send SMS right now. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public void sendOtpSms(String phoneNumber, String otp) {
        sendSms(phoneNumber, "Your Bueno Exports verification code is " + otp + ". It expires in 5 minutes.");
    }
}
