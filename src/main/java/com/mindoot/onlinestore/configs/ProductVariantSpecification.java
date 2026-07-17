package com.mindoot.onlinestore.configs;

import com.mindoot.onlinestore.model.ProductVariant;
import org.springframework.data.jpa.domain.Specification;

public class ProductVariantSpecification {

    public static Specification<ProductVariant> matchesFilters(
            String brand,
            String weight,
            String unit,
            Boolean wholesaleEnabled,
            Boolean active
    ) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (brand != null && !brand.isEmpty())
                predicates.getExpressions().add(cb.equal(cb.lower(root.get("product").get("brand")), brand.toLowerCase()));

            if (weight != null && !weight.isEmpty())
                predicates.getExpressions().add(cb.equal(cb.lower(root.get("weight")), weight.toLowerCase()));

            if (unit != null && !unit.isEmpty())
                predicates.getExpressions().add(cb.equal(cb.lower(root.get("unit")), unit.toLowerCase()));

            if (wholesaleEnabled != null)
                predicates.getExpressions().add(cb.equal(root.get("wholesaleEnabled"), wholesaleEnabled));

            if (active != null)
                predicates.getExpressions().add(cb.equal(root.get("active"), active));

            return predicates;
        };
    }
}
