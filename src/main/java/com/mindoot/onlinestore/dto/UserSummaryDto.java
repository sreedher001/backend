package com.mindoot.onlinestore.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryDto {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private List<String> roles;
    private boolean enabled;
    private boolean emailVerified;
    private boolean phoneNumberVerified;
    private String preferredPurchaseType;
    private LocalDate createdOn;
}
