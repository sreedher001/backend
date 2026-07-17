package com.mindoot.onlinestore.service.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.model.Product;
import com.mindoot.onlinestore.model.ProductSearchIndex;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.repository.ProductSearchIndexRepository;
import com.mindoot.onlinestore.service.ProductIndexService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductIndexServiceImpl implements ProductIndexService {

    private final ProductSearchIndexRepository repository;

    public void indexProduct(Product product) {
        List<ProductSearchIndex> rows = new ArrayList<>();

        for (ProductVariant variant : product.getVariants()) {
            ProductSearchIndex row = new ProductSearchIndex();

            row.setProductId(product.getId());
            row.setVariantId(variant.getId());
            row.setProductName(product.getName());
            row.setVariantName(variant.getVariantName());
            row.setSlug(product.getSlug());
            row.setCategoryId(product.getCategoryId());
            row.setBrand(product.getBrand());
            row.setTags(product.getTags());
            row.setWeight(variant.getWeight());
            row.setUnit(variant.getUnit());
            row.setRetailPrice(variant.getRetailPrice());
            row.setWholesalePrice(variant.getWholesalePrice());
            row.setRating(variant.getRating());
            row.setIsFeatured(variant.getIsFeatured());
            row.setActive(variant.getActive());
            row.setImageUrl(variant.getImageUrl());
            row.setInStock(variant.getInventory() != null && variant.getInventory().getAvailableQuantity() > 0);
            row.setSku(variant.getSku());

            rows.add(row);
        }

        repository.saveAll(rows);
    }
}
