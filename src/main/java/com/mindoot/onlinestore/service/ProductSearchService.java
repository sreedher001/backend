package com.mindoot.onlinestore.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.ProductSearchRequestDto;
import com.mindoot.onlinestore.model.ProductSearchIndex;

@Component
public interface ProductSearchService {

	public Page<ProductSearchIndex> search(ProductSearchRequestDto request);
	public void refreshSearchIndex(Long variantId);
}
