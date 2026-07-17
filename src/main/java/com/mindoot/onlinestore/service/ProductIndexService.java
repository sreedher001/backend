package com.mindoot.onlinestore.service;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.model.Product;

@Component
public interface ProductIndexService {

	public void indexProduct(Product product);
}
