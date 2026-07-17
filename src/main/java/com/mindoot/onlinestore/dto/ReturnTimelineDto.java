package com.mindoot.onlinestore.dto;

import java.time.LocalDateTime;

import com.mindoot.onlinestore.enums.ReturnStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReturnTimelineDto {

	private ReturnStatus status;
    private String changedBy;
    private LocalDateTime changedAt;
}
