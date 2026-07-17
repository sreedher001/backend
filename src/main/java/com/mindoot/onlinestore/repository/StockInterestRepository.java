package com.mindoot.onlinestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.StockInterest;
import com.mindoot.onlinestore.repository.projection.StockInterestSummaryProjection;

@Repository
public interface StockInterestRepository extends JpaRepository<StockInterest, Long> {

	boolean existsByVariantIdAndUserIdAndNotifiedFalse(Long variantId, Long userId);

	boolean existsByVariantIdAndEmailAndNotifiedFalse(Long variantId, String email);

	List<StockInterest> findByVariantIdAndNotifiedFalse(Long variantId);

	@Query("""
		    SELECT si.variantId as variantId,
		           COUNT(si.id) as waitingCount,
		           MIN(si.createdAt) as oldestRequest
		    FROM StockInterest si
		    WHERE si.notified = false
		    GROUP BY si.variantId
		    ORDER BY COUNT(si.id) DESC
		""")
		List<StockInterestSummaryProjection> getStockInterestSummary();
}
