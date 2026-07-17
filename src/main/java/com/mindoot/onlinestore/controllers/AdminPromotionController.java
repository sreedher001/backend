package com.mindoot.onlinestore.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.promotiondto.PromotionCreateRequestDto;
import com.mindoot.onlinestore.dto.promotiondto.PromotionDto;
import com.mindoot.onlinestore.service.PromotionEngineService;

@RestController
@RequestMapping("/api/admin/promotions")
public class AdminPromotionController {

	@Autowired
    private PromotionEngineService promotionService;

    @PostMapping("/create")
    public PromotionDto createPromotion(@RequestBody PromotionCreateRequestDto request) {
        return promotionService.createPromotion(request);
    }

    @PostMapping("/update/{id}")
    public PromotionDto updatePromotion(
            @PathVariable("id") Long id,
            @RequestBody PromotionCreateRequestDto request) {

        return promotionService.updatePromotion(id, request);
    }

    @GetMapping("/allPromotions")
    public List<PromotionDto> getPromotions() {
        return promotionService.getAllPromotions();
    }

    @PostMapping("/delete/{id}")
    public void deletePromotion(@PathVariable("id") Long id) {
        promotionService.deletePromotion(id);
    }
}
