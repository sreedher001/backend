package com.mindoot.onlinestore.dto.courierdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingDto {

	private String orderNumber;
	public String comment;
	private Double length;
    private Double breadth;
    private Double height;
    private Double weight;
    private Integer giftwrapCharge;
    private Integer transactionCharges;
    private String pickupLocation;
    
}
