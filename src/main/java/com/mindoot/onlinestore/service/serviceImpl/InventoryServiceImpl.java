package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.enums.InventoryStatus;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.Inventory;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.repository.InventoryRepository;
import com.mindoot.onlinestore.repository.ProductVariantRepository;
import com.mindoot.onlinestore.service.InventoryService;

import jakarta.transaction.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private ProductVariantRepository productVariantRepository;

	@Override
	public void initializeInventoryForVariant(ProductVariant variant, int quantity) {
		Inventory inventory = new Inventory();
		inventory.setVariant(variant);
		inventory.setAvailableQuantity(quantity);
		inventory.setReservedQuantity(0);
		inventory.setLowStockThreshold(3);
		inventory.setInventoryStatus(determineStatus(quantity));
		inventory.setLastUpdated(LocalDateTime.now());

		inventoryRepository.save(inventory);
	}

	@Override
	@Transactional
	public boolean reserveStock(Long variantId, int quantity) {
		Inventory inventory = inventoryRepository.findByVariantIdForUpdate(variantId)
			.orElseThrow(() -> new ApplicationException("Inventory not found for variant", HttpStatus.NOT_FOUND));

		if (inventory.getAvailableQuantity() < quantity) {
			return false;
		}

		inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
		inventory.setReservedQuantity(Optional.ofNullable(inventory.getReservedQuantity()).orElse(0) + quantity);
		inventory.setLastUpdated(LocalDateTime.now());
		inventory.setInventoryStatus(determineStatus(inventory.getAvailableQuantity()));

		inventoryRepository.save(inventory);
		return true;
	}

	@Override
	@Transactional
	public boolean releaseReservedStock(Long variantId, int quantity) {
		Inventory inventory = inventoryRepository.findByVariantId(variantId)
			.orElseThrow(() -> new ApplicationException("Inventory not found for variant", HttpStatus.NOT_FOUND));

		if (Optional.ofNullable(inventory.getReservedQuantity()).orElse(0) >= quantity) {
			inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
			inventory.setReservedQuantity(Optional.ofNullable(inventory.getReservedQuantity()).orElse(0) - quantity);
			inventory.setLastUpdated(LocalDateTime.now());
			inventory.setInventoryStatus(determineStatus(inventory.getAvailableQuantity()));
			inventoryRepository.save(inventory);
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public void confirmSale(Long variantId, int quantity) {
		Inventory inventory = inventoryRepository.findByVariantId(variantId)
			.orElseThrow(() -> new ApplicationException("Inventory not found for variant", HttpStatus.NOT_FOUND));

		if (Optional.ofNullable(inventory.getReservedQuantity()).orElse(0) < quantity) {
			throw new ApplicationException(
				"Not enough reserved quantity. Available: " + Optional.ofNullable(inventory.getReservedQuantity()).orElse(0),
				HttpStatus.BAD_REQUEST);
		}

		inventory.setReservedQuantity(Optional.ofNullable(inventory.getReservedQuantity()).orElse(0) - quantity);
		inventory.setLastUpdated(LocalDateTime.now());
		inventory.setInventoryStatus(determineStatus(inventory.getAvailableQuantity()));
		inventoryRepository.save(inventory);
	}

	public static InventoryStatus determineStatus(int availableQty) {
		if (availableQty == 0) return InventoryStatus.OUT_OF_STOCK;
		if (availableQty <= 3) return InventoryStatus.LOW_STOCK;
		return InventoryStatus.IN_STOCK;
	}
}
