package com.mindoot.onlinestore.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.ProductAutocompleteDto;
import com.mindoot.onlinestore.dto.ProductFilterDto;
import com.mindoot.onlinestore.dto.ProductResponseDto;
import com.mindoot.onlinestore.service.ProductService;


// TODO: Auto-generated Javadoc
/**
 * The Class ProductController.
 */
@RestController
@RequestMapping("/api/products")
class ProductController {

	/** The product service. */
	@Autowired
    private ProductService productService;

    /**
     * Gets the all products.
     *
     * @param page the page
     * @param size the size
     * @return the all products
     */
    @GetMapping("/all-products")
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name="size" , defaultValue = "12") int size) {
        // Public, unauthenticated endpoint used by the storefront - always
        // active-only. AuthTokenFilter treats this path as public and skips
        // JWT parsing for it, so there's no reliable way to tell an admin
        // caller apart from a shopper here; admin tooling that needs
        // inactive products uses /api/admin/products/list instead, which is
        // properly authenticated and ROLE_ADMIN-gated.
        return ResponseEntity.ok(productService.getAllProducts(page, size, true));
    }
    
    
    /**
     * Search products.
     *
     * @param query the query
     * @return the response entity
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> searchProducts(@RequestParam("query") String query,
    		@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name="size" , defaultValue = "12") int size) {
        return ResponseEntity.ok(productService.searchProducts(query,page,size));
    }
    
    @GetMapping("/search/autocomplete")
    public ResponseEntity<List<ProductAutocompleteDto>> autocomplete(@RequestParam("query") String query) {
        List<ProductAutocompleteDto> suggestions = productService.autocompleteProducts(query);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Best-selling products ranked by real order volume (falls back to
     * featured/highest-rated products when there's no order history yet).
     */
    @GetMapping("/best-sellers")
    public ResponseEntity<List<ProductResponseDto>> getBestSellers(
            @RequestParam(name = "limit", defaultValue = "8") int limit) {
        return ResponseEntity.ok(productService.getBestSellers(limit));
    }


    /**
     * Gets the product by id.
     *
     * @param id the id
     * @return the product by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("id") Long id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Filter products.
     *
     * @param filterDto the filter dto
     * @return the response entity
     */
    @PostMapping("/search/filter")
    public ResponseEntity<List<ProductResponseDto>> filterProducts(@RequestBody ProductFilterDto filterDto) {
        List<ProductResponseDto> filtered = productService.filterProducts(filterDto);
        return ResponseEntity.ok(filtered);
    }
    
    @GetMapping("/variants/{variantId}")
    public ResponseEntity<ProductResponseDto> getVariantById(
            @PathVariable("variantId") Long variantId) {

    	ProductResponseDto response= productService.findByVariantId(variantId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/variants/slug/{slug}")
    public ResponseEntity<ProductResponseDto> getVariantBySlug(
            @PathVariable("slug") String slug) {

    	ProductResponseDto response = productService.findByVariantSlug(slug);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/variants/{variantId}/similar")
    public ResponseEntity<ProductResponseDto> getAllSimilarItems(
            @PathVariable("variantId") Long variantId) {

    	ProductResponseDto response= productService.getAllSimilarItems(variantId);
        
        return ResponseEntity.ok(response);
    }
    
    


}
