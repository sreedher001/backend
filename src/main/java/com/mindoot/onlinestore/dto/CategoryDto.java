package com.mindoot.onlinestore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {

	private Long id;
	private String name;
	private String slug;
	private String description;
	private String imageUrl;
	private Long parentId;
	private Integer sortOrder;
	private Boolean active;
}
