package com.mindoot.onlinestore.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RazorpayOrderResponse {
    private String razorpayOrderId;
    private String currency;
    private Integer amount; // in paisa
    private String name;
    private String email;
    private String phone;
    private String key;
}

