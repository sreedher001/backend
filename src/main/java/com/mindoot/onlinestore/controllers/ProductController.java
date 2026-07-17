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
        return ResponseEntity.ok(productService.getAllProducts(page, size));
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
     * Gets the product by id.
     *
     * @param id the id
     * @return the product by id
     */
    @GetMapping("/search/{id}")
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
    
    @PostMapping("/variants/{variantId}/similar")
    public ResponseEntity<ProductResponseDto> getAllSimilarItems(
            @PathVariable("variantId") Long variantId) {

    	ProductResponseDto response= productService.getAllSimilarItems(variantId);
        
        return ResponseEntity.ok(response);
    }
    
    


}
