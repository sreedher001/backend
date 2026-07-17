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
	    category_id,
	    brand,
	    tags,
	    weight,
	    unit,
	    retail_price,
	    wholesale_price,
	    rating,
	    is_featured,
	    active,
	    image_url,
	    in_stock,
	    sku
	)
	SELECT
	    p.id,
	    v.id,
	    p.name,
	    v.variant_name,
	    p.slug,
	    p.category_id,
	    p.brand,
	    p.tags,
	    v.weight,
	    v.unit,
	    v.retail_price,
	    v.wholesale_price,
	    v.rating,
	    v.is_featured,
	    v.active,
	    v.image_url,
	    CASE WHEN v.id IN (SELECT i.variant_id FROM inventory i WHERE i.available_quantity > 0) THEN true ELSE false END,
	    v.sku
	FROM products p
	JOIN product_variants v ON v.product_id = p.id
	WHERE v.id = :variantId
	""", nativeQuery = true)
	void insertIndexByVariantId(@Param("variantId") Long variantId);
}
