package com.mindoot.onlinestore.dto;

import lombok.Data;

@Data
public class PaymentVerificationRequest {
    private String orderNumber;             // your internal order number
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
}

