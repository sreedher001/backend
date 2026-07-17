package com.mindoot.onlinestore.dto;

import java.math.BigDecimal;

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
public class InvoiceItemDto {

	private Long id;
	 private String itemName;           
	    private BigDecimal quantity;          
	    private BigDecimal price;  
	    private BigDecimal totalPrice;
}
