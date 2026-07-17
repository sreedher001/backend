package com.mindoot.onlinestore.service;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.PaymentVerificationRequest;
import com.mindoot.onlinestore.dto.RazorpayOrderResponse;
import com.mindoot.onlinestore.model.User;
import com.razorpay.RazorpayException;

@Component
public interface RazorpayService {

	public String createOrder(int amount, String currency, String reciptId) throws RazorpayException;

	public RazorpayOrderResponse createRazorpayOrder(String orderNumber, User user, int intValue) throws RazorpayException;

	public void verifyAndCapturePayment(PaymentVerificationRequest request);
}
