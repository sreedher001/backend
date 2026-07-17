package com.mindoot.onlinestore.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.ShippingRuleRequestDTO;
import com.mindoot.onlinestore.dto.ShippingRuleResponseDTO;
import com.mindoot.onlinestore.model.ShippingRule;

@Component
public interface ShippingService {

	public double calculateShippingFee(double subTotal);
	public ShippingRuleResponseDTO save(ShippingRuleRequestDTO dto);
	public ShippingRuleResponseDTO getById(Long id);
	public List<ShippingRuleResponseDTO> getAll();
	public void delete(Long id);
	public ShippingRuleResponseDTO update(Long id, ShippingRuleRequestDTO dto);
}
