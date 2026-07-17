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

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
