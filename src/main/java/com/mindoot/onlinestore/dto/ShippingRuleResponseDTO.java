package com.mindoot.onlinestore.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingRuleResponseDTO {

    private Long id;
    private String name;
    private Double minCartValue;
    private Double maxCartValue;
    private Double shippingFee;
    private Boolean freeShipping;
    private Integer priority;
    private Boolean active;
}
