package com.mindoot.onlinestore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequest {
    private String field; // "email" or "phone"
    private String value; // new email or phone number
}
