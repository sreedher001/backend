package com.mindoot.onlinestore.service;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.model.ProductVariant;

@Component
public interface InventoryService {

	public void initializeInventoryForVariant(ProductVariant variant, int quantity);

	public boolean reserveStock(Long variantId, int qty);

	public void confirmSale(Long variantId, int qty);

	public boolean releaseReservedStock(Long variantId, int qty);
}
