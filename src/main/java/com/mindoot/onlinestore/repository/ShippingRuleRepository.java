package com.mindoot.onlinestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.ShippingRule;

@Repository
public interface ShippingRuleRepository extends JpaRepository<ShippingRule, Long> {

//	List<ShippingRule> findActiveRulesOrderByPriority();

	List<ShippingRule> findByActiveTrueOrderByPriorityAsc();

}
