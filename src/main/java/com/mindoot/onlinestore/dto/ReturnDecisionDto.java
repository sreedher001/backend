package com.mindoot.onlinestore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnDecisionDto {
    private boolean approve;
    private String rejectionReason;
}

