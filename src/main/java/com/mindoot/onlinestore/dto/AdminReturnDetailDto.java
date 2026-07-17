package com.mindoot.onlinestore.dto;

import java.util.List;

import com.mindoot.onlinestore.enums.ReturnStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminReturnDetailDto {

    private Long returnId;
    private String orderNumber;
    private String customerName;
    private String email;
    private String phone;

    private String productName;
    private String variantName;
    private Integer quantity;

    private String reason;
    private String comment;
    private ReturnStatus status;

    private List<ReturnTimelineDto> timeline;
}
