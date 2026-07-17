package com.mindoot.onlinestore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.Product;
import com.mindoot.onlinestore.model.VariantRatingSummary;

@Repository
public interface VariantRatingSummaryRepository
        extends JpaRepository<VariantRatingSummary, Long> {

	Optional<VariantRatingSummary> findByVariantId(Long id);
}
