package com.mindoot.onlinestore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.promotion.PromotionAction;

@Repository
public interface PromotionActionRepository extends JpaRepository<PromotionAction, Long> {

	 Optional<PromotionAction> findByPromotionId(Long promotionId);

	void deleteByPromotionId(Long id);

}
