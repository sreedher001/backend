package com.mindoot.onlinestore.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddressDTO {
    private Long id;
    private String name;
    private String country;
    private String state;
    private String pinCode;
    private String address;
    private String city;
    private String phoneNumber;
    private boolean isDefault;
}
