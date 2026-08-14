package com.mindoot.onlinestore.configs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import com.mindoot.onlinestore.dto.ProductSearchRequestDto;
import com.mindoot.onlinestore.model.ProductSearchIndex;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;

public class ProductSearchSpecification {

	public static Specification<ProductSearchIndex> build(ProductSearchRequestDto request) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			// This index powers the public storefront's browse/search page;
			// deactivated products must never surface here regardless of
			// what other filters the caller passes.
			predicates.add(cb.equal(root.get("active"), true));

			if (request.getFilters() != null) {

				for (Map.Entry<String, List<String>> entry : request.getFilters().entrySet()) {

					String key = entry.getKey();
					List<String> values = entry.getValue();

					if (values != null && !values.isEmpty()) {
						CriteriaBuilder.In<String> inClause =
							cb.in(cb.lower(root.get(key)));

						for (String value : values) {
							inClause.value(value.toLowerCase());
						}

						predicates.add(inClause);
					}
				}
			}

			if (request.getMinPrice() != null && request.getMaxPrice() != null) {
				predicates.add(
					cb.between(
						root.get("retailPrice"),
						request.getMinPrice(),
						request.getMaxPrice()
					)
				);
			}

			if (request.getInStock() != null) {
				predicates.add(cb.equal(root.get("inStock"), request.getInStock()));
			}

			if (request.getQuery() != null && !request.getQuery().isBlank()) {
				String likeValue = "%" + request.getQuery().toLowerCase() + "%";
				predicates.add(cb.or(
					cb.like(cb.lower(root.get("productName")), likeValue),
					cb.like(cb.lower(root.get("variantName")), likeValue),
					cb.like(cb.lower(root.get("brand")), likeValue),
					cb.like(cb.lower(root.get("tags")), likeValue),
					cb.like(cb.lower(root.get("categoryName")), likeValue)
				));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
