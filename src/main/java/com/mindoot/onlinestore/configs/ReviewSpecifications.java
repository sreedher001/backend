package com.mindoot.onlinestore.configs;

import org.springframework.data.jpa.domain.Specification;

import com.mindoot.onlinestore.model.ProductReview;

public class ReviewSpecifications {

    public static Specification<ProductReview> approved(Boolean approved) {
        return (root, query, cb) ->
                approved == null ? null : cb.equal(root.get("approved"), approved);
    }

    public static Specification<ProductReview> rating(Integer rating) {
        return (root, query, cb) ->
                rating == null ? null : cb.equal(root.get("rating"), rating);
    }

    public static Specification<ProductReview> productId(Long productId) {
        return (root, query, cb) ->
                productId == null ? null :
                        cb.equal(root.get("variant").get("product").get("id"), productId);
    }

    public static Specification<ProductReview> variantId(Long variantId) {
        return (root, query, cb) ->
                variantId == null ? null :
                        cb.equal(root.get("variant").get("id"), variantId);
    }
}

