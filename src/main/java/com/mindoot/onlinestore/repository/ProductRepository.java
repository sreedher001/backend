package com.mindoot.onlinestore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    Optional<Product> findBySku(String sku);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    boolean existsByBarcode(String barcode);

    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    List<Product> findByActiveTrue();

    List<Product> findByIsFeaturedTrueAndActiveTrue();

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p.id, p.name FROM Product p")
    List<Object[]> findAllProductIdsAndNames();

    @Query(value = """
        SELECT p.*
        FROM products p
        WHERE MATCH(p.name, p.short_description, p.brand, p.tags)
            AGAINST(:query IN NATURAL LANGUAGE MODE)
        ORDER BY p.uploaded_at DESC
        """, countQuery = """
        SELECT COUNT(*)
        FROM products p
        WHERE MATCH(p.name, p.short_description, p.brand, p.tags)
            AGAINST(:query IN NATURAL LANGUAGE MODE)
        """, nativeQuery = true)
    Page<Product> searchFullText(@Param("query") String query, Pageable pageable);

    @Query(value = """
        SELECT *
        FROM products
        WHERE active = true
          AND (
            LOWER(name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(sku) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(brand) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(tags) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(short_description) LIKE LOWER(CONCAT('%', :query, '%'))
          )
        LIMIT 20
    """, nativeQuery = true)
    Page<Product> searchFallback(@Param("query") String query, Pageable pageable);

    @Query(value = """
        SELECT DISTINCT
            p.id, p.name, p.slug, p.brand,
            pv.variant_name, pv.weight, pv.unit, pv.sku
        FROM products p
        JOIN product_variants pv ON pv.product_id = p.id
        WHERE p.active = true
          AND (
            LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(pv.variant_name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(pv.sku) LIKE LOWER(CONCAT('%', :query, '%'))
          )
        ORDER BY p.name ASC
        LIMIT 20
    """, nativeQuery = true)
    List<Object[]> searchForAutocomplete(@Param("query") String query);

    @Query("SELECT p FROM Product p WHERE p.categoryId = :categoryId AND p.subCategoryId = :subCategoryId AND p.active = true")
    List<Product> findByCategoryAndSubCategory(@Param("categoryId") Long categoryId, @Param("subCategoryId") Long subCategoryId);
}
