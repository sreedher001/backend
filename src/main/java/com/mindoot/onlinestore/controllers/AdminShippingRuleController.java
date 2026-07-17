package com.mindoot.onlinestore.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.ShippingRuleRequestDTO;
import com.mindoot.onlinestore.dto.ShippingRuleResponseDTO;
import com.mindoot.onlinestore.service.ShippingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/shipping-rules")
public class AdminShippingRuleController {

	@Autowired
    private  ShippingService service;

    @PostMapping("/create")
    public ShippingRuleResponseDTO save(@Valid @RequestBody ShippingRuleRequestDTO dto) {
        return service.save(dto);
    }

    @GetMapping("/all-shipping-rules")
    public List<ShippingRuleResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ShippingRuleResponseDTO getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }
    
    @PostMapping("/update/{id}")
    public ShippingRuleResponseDTO update(
            @PathVariable("id") Long id,
            @Valid @RequestBody ShippingRuleRequestDTO dto) {

        return service.update(id, dto);
    }
    
    @PostMapping("/delete/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
