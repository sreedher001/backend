package com.mindoot.onlinestore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.ProductReview;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.model.User;

@Repository
public interface ProductReviewRepository
        extends JpaRepository<ProductReview, Long>,JpaSpecificationExecutor<ProductReview> {

    boolean existsByUserAndVariant(User user, ProductVariant variant);

    @Query("""
        SELECT r FROM ProductReview r
        WHERE r.variant.id = :variantId
        AND r.approved = true
    """)
    List<ProductReview> findApprovedByProductId(@Param("variantId") Long variantId);

    List<ProductReview> findByVariantIdAndApprovedTrue(Long variantId);
    
    @Query("SELECT r FROM ProductReview r WHERE r.variant.id = :variantId ORDER BY r.createdAt DESC")
        List<ProductReview> findByProductId(@Param("variantId") Long variantId);

	Page<ProductReview> findByVariantIdAndApprovedTrueAndRating(Long variantId, Integer rating, Pageable pageable);

	Page<ProductReview> findByVariantIdAndApprovedTrue(Long variantId, Pageable pageable);

	boolean existsByUserIdAndVariantId(Long userId, Long variantId);
}

