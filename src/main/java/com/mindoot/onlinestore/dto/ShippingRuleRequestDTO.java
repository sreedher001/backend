package com.mindoot.onlinestore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingRuleRequestDTO {

    @NotBlank
    private String name;

    @Min(0)
    private Double minCartValue;

    private Double maxCartValue;

    @Min(0)
    private Double shippingFee;

    private Boolean freeShipping;

    private Integer priority;

    private Boolean active;
}
