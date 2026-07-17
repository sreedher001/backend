package com.mindoot.onlinestore.service.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.ShippingRuleRequestDTO;
import com.mindoot.onlinestore.dto.ShippingRuleResponseDTO;
import com.mindoot.onlinestore.model.ShippingRule;
import com.mindoot.onlinestore.repository.ShippingRuleRepository;
import com.mindoot.onlinestore.service.ShippingService;

@Service
public class ShippingServiceImpl implements ShippingService{

	@Autowired
	private ShippingRuleRepository shippingRuleRepository;
	

	@Override
	    public double calculateShippingFee(double subtotal) {

//	        double subtotal = order.getSubtotal();

	        List<ShippingRule> rules =
	                shippingRuleRepository.findByActiveTrueOrderByPriorityAsc();
	        System.out.println("rules :"+rules.toString());

	        for (ShippingRule rule : rules) {

	            if (subtotal >= rule.getMinCartValue() &&
	               (rule.getMaxCartValue() == null ||
	                subtotal <= rule.getMaxCartValue())) {

	                if(Boolean.TRUE.equals(rule.getFreeShipping())){
	                    return 0;
	                }

	                return rule.getShippingFee();
	            }
	        }

	        return 0;
	    
	}
	
	 public ShippingRuleResponseDTO save(ShippingRuleRequestDTO dto) {

	        ShippingRule rule = new ShippingRule();

	        rule.setName(dto.getName());
	        rule.setMinCartValue(dto.getMinCartValue());
	        rule.setMaxCartValue(dto.getMaxCartValue());
	        rule.setShippingFee(dto.getShippingFee());
	        rule.setFreeShipping(dto.getFreeShipping());
	        rule.setPriority(dto.getPriority());
	        rule.setActive(dto.getActive());

	        ShippingRule saved = shippingRuleRepository.save(rule);

	        return mapToResponse(saved);
	    }

	    public List<ShippingRuleResponseDTO> getAll() {

	        return shippingRuleRepository.findAll(Sort.by("priority").ascending())
	                .stream()
	                .map(this::mapToResponse)
	                .collect(Collectors.toList());
	    }

	    public ShippingRuleResponseDTO getById(Long id) {

	        ShippingRule rule = shippingRuleRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Shipping rule not found"));

	        return mapToResponse(rule);
	    }

	    private ShippingRuleResponseDTO mapToResponse(ShippingRule rule) {

	        return ShippingRuleResponseDTO.builder()
	                .id(rule.getId())
	                .name(rule.getName())
	                .minCartValue(rule.getMinCartValue())
	                .maxCartValue(rule.getMaxCartValue())
	                .shippingFee(rule.getShippingFee())
	                .freeShipping(rule.getFreeShipping())
	                .priority(rule.getPriority())
	                .active(rule.getActive())
	                .build();
	    }
	    public ShippingRuleResponseDTO update(Long id, ShippingRuleRequestDTO dto) {

	        ShippingRule rule = shippingRuleRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Shipping rule not found"));

	        rule.setName(dto.getName());
	        rule.setMinCartValue(dto.getMinCartValue());
	        rule.setMaxCartValue(dto.getMaxCartValue());
	        rule.setShippingFee(dto.getShippingFee());
	        rule.setFreeShipping(dto.getFreeShipping());
	        rule.setPriority(dto.getPriority());
	        rule.setActive(dto.getActive());

	        ShippingRule updated = shippingRuleRepository.save(rule);

	        return mapToResponse(updated);
	    }
	    
	    public void delete(Long id) {

	        ShippingRule rule = shippingRuleRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Shipping rule not found"));

	        shippingRuleRepository.delete(rule);
	    }
}
