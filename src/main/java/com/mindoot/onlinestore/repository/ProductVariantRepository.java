package com.mindoot.onlinestore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductIdAndActiveTrue(Long productId);

    List<ProductVariant> findByProductId(Long productId);

    Optional<ProductVariant> findBySku(String sku);

    boolean existsBySku(String sku);

    boolean existsByBarcode(String barcode);

    Optional<ProductVariant> findByProductIdAndWeightAndUnit(Long productId, String weight, String unit);

    Optional<ProductVariant> findBySlug(String slug);

    boolean existsBySlug(String slug);

    /** Fallback for "Best Sellers" when there's no order history yet. */
    List<ProductVariant> findTop12ByActiveTrueOrderByIsFeaturedDescRatingDesc();
}
