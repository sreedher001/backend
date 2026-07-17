package com.mindoot.onlinestore.dto;

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
public class AvailableSizesDto {

	private Long id;
	private String size;
	private Integer availableQuantity;
	private Double price;
	private Double discountPercentage;
	private Double priceAfterDiscount;
}
