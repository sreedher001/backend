package com.mindoot.onlinestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.promotion.PromotionCondition;

@Repository
public interface PromotionConditionRepository extends JpaRepository<PromotionCondition, Long> {

	List<PromotionCondition> findByPromotionId(Long id);

	void deleteByPromotionId(Long id);

}
