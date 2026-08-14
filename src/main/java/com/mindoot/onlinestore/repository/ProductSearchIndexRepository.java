package com.mindoot.onlinestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.ProductSearchIndex;

@Repository
public interface ProductSearchIndexRepository extends
        JpaRepository<ProductSearchIndex, Long>,
        JpaSpecificationExecutor<ProductSearchIndex> {

	@Modifying
	@Query(value = "DELETE FROM product_search_index WHERE variant_id = :variantId", nativeQuery = true)
	void deleteByVariantId(@Param("variantId") Long variantId);

	@Modifying
	@Query(value = """
	INSERT INTO product_search_index (
	    product_id,
	    variant_id,
	    product_name,
	    variant_name,
	    slug,
	    variant_slug,
	    category_id,
	    category_name,
	    sub_category_id,
	    sub_category_name,
	    brand,
	    tags,
	    weight,
	    unit,
	    retail_price,
	    mrp,
	    wholesale_price,
	    rating,
	    is_featured,
	    active,
	    image_url,
	    in_stock,
	    sku,
	    retail_enabled,
	    wholesale_enabled
	)
	SELECT
	    p.id,
	    v.id,
	    p.name,
	    v.variant_name,
	    p.slug,
	    v.slug,
	    p.category_id,
	    c.name,
	    p.sub_category_id,
	    sc.name,
	    p.brand,
	    p.tags,
	    v.weight,
	    v.unit,
	    v.retail_price,
	    v.mrp,
	    v.wholesale_price,
	    v.rating,
	    v.is_featured,
	    (p.active = true AND v.active = true),
	    v.image_url,
	    CASE WHEN v.id IN (SELECT i.variant_id FROM inventory i WHERE i.available_quantity > 0) THEN true ELSE false END,
	    v.sku,
	    v.retail_enabled,
	    v.wholesale_enabled
	FROM products p
	JOIN product_variants v ON v.product_id = p.id
	LEFT JOIN categories c ON c.id = p.category_id
	LEFT JOIN categories sc ON sc.id = p.sub_category_id
	WHERE v.id = :variantId
	""", nativeQuery = true)
	void insertIndexByVariantId(@Param("variantId") Long variantId);
}
