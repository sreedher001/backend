package com.mindoot.onlinestore.controllers;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.ProductSearchRequestDto;
import com.mindoot.onlinestore.model.ProductSearchIndex;
import com.mindoot.onlinestore.service.ProductSearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products/category")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService service;

    @PostMapping("/search")
    public Page<ProductSearchIndex> search(@RequestBody ProductSearchRequestDto request){

        return service.search(request);

    }

}
