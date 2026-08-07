package com.mindoot.onlinestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	interface TopSellingVariant {
		Long getVariantId();
		Long getTotalQty();
	}

	/**
	 * Ranks variants by total quantity sold across all non-cancelled/returned
	 * orders, so "Best Sellers" reflects genuine sales rather than a manually
	 * curated flag.
	 */
	@Query(value = """
		SELECT oi.variant_id AS variantId, SUM(oi.quantity) AS totalQty
		FROM order_items oi
		JOIN orders o ON oi.order_id = o.id
		WHERE oi.variant_id IS NOT NULL
		  AND o.status NOT IN ('CANCELLED','REJECTED','RETURN_INITIATED','RETURNED')
		GROUP BY oi.variant_id
		ORDER BY totalQty DESC
		LIMIT :limit
		""", nativeQuery = true)
	List<TopSellingVariant> findTopSellingVariants(@Param("limit") int limit);
}
