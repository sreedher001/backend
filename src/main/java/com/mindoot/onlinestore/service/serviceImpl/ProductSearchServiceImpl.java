package com.mindoot.onlinestore.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.configs.ProductSearchSpecification;
import com.mindoot.onlinestore.dto.ProductSearchRequestDto;
import com.mindoot.onlinestore.model.ProductSearchIndex;
import com.mindoot.onlinestore.repository.ProductSearchIndexRepository;
import com.mindoot.onlinestore.service.ProductSearchService;

import jakarta.transaction.Transactional;

@Service
public class ProductSearchServiceImpl implements ProductSearchService {

	@Autowired
	private ProductSearchIndexRepository repository;

	@Override
	public Page<ProductSearchIndex> search(ProductSearchRequestDto request) {

		Specification<ProductSearchIndex> spec = ProductSearchSpecification.build(request);

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), getSort(request.getSort()));

		return repository.findAll(spec, pageable);
	}

	private Sort getSort(String sort) {

		if (sort == null)
			return Sort.by("isFeatured").descending();

		return switch (sort) {

		case "featured" -> Sort.by("isFeatured").descending();

		case "priceAsc", "priceLowHigh" -> Sort.by("finalPrice").ascending();

		case "priceDesc", "priceHighLow" -> Sort.by("finalPrice").descending();

		case "rating" -> Sort.by("rating").descending();

		case "new", "newArrival" -> Sort.by("finalPrice").descending();

		case "bestselling" -> Sort.by("isFeatured").descending(); // or any field you use

		default -> Sort.by("isFeatured").descending();
		};
	}
	
	@Transactional
	public void refreshSearchIndex(Long variantId) {

		repository.deleteByVariantId(variantId);

		repository.insertIndexByVariantId(variantId);
	}
}
