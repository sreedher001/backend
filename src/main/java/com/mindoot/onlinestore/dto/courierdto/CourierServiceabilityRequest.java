package com.mindoot.onlinestore.dto.courierdto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourierServiceabilityRequest {

	private String pickupPostcode;
	private String deliveryPostcode;
	private int cod;
	private double weight;
	//this is shiprocket order id
	private Long shipRocketOrderId;
	private String orderNumber;
}
