package com.mindoot.onlinestore.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.mindoot.onlinestore.dto.ProductFilterDto;
import com.mindoot.onlinestore.model.Product;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

	public static Specification<Product> multiFieldSearch(String query) {
		return (root, cq, cb) -> {
			String[] keywords = query.toLowerCase().split("\\s+");

			List<Predicate> predicates = new ArrayList<>();
			for (String keyword : keywords) {
				Predicate wordPredicate = cb.or(
					cb.like(cb.lower(root.get("name")), "%" + keyword + "%"),
					cb.like(cb.lower(root.get("shortDescription")), "%" + keyword + "%"),
					cb.like(cb.lower(root.get("longDescription")), "%" + keyword + "%"),
					cb.like(cb.lower(root.get("brand")), "%" + keyword + "%"),
					cb.like(cb.lower(root.get("tags")), "%" + keyword + "%"));
				predicates.add(wordPredicate);
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
