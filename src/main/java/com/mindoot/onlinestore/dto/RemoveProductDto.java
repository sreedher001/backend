package com.mindoot.onlinestore.dto;

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
public class RemoveProductDto {

	private String objectKey;
	private Long id;
	private boolean isDeleteEntireProduct;
	private Long variantId;
}
