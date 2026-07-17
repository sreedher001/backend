package com.mindoot.onlinestore.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.imaging.ImageReadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mindoot.onlinestore.dto.AddVariantRequest;
import com.mindoot.onlinestore.dto.ProductResponseDto;
import com.mindoot.onlinestore.dto.ProductWithVariantsRequest;
import com.mindoot.onlinestore.dto.UpdateProductRequest;
import com.mindoot.onlinestore.dto.UpdateVariantRequest;
import com.mindoot.onlinestore.model.Category;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.service.CategoryService;
import com.mindoot.onlinestore.service.ExcelImportService;
import com.mindoot.onlinestore.service.ProductService;
import com.mindoot.onlinestore.utility.TokenUtils;
import com.mindoot.onlinestore.utility.UserInfo;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/admin/products/")
@Slf4j
public class ProductAdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private ExcelImportService excelImportService;

    @PostMapping(value = "images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> uploadProduct(
            @RequestPart("file") MultipartFile[] files,
            @RequestPart("metadata") String metadataJson,
            @RequestHeader("Authorization") String token) throws IOException, ImageReadException {

        UserInfo userInfo = tokenUtils.getUserInfo(token.substring(7));
        productService.createProduct(metadataJson, files, userInfo);
        return ResponseEntity.ok(new MessageResponse("Product created successfully!", HttpStatus.OK));
    }

    @PostMapping(value = "create-with-variants", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<MessageResponse> createProductWithVariants(
            @RequestPart("metadata") String metadataJson,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestHeader("Authorization") String token) throws IOException {

        UserInfo userInfo = tokenUtils.getUserInfo(token.substring(7));
        ProductWithVariantsRequest request = new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(metadataJson, ProductWithVariantsRequest.class);
        List<MultipartFile> imageList = files != null ? List.of(files) : List.of();
        productService.createProductWithVariants(request, imageList, userInfo);
        return ResponseEntity.ok(new MessageResponse("Product with variants created successfully!", HttpStatus.OK));
    }

    @PutMapping(value = "{productId}/update-with-variants", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<MessageResponse> updateProductWithVariants(
            @PathVariable("productId") Long productId,
            @RequestPart("metadata") String metadataJson,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestHeader("Authorization") String token) throws IOException {

        UserInfo userInfo = tokenUtils.getUserInfo(token.substring(7));
        ProductWithVariantsRequest request = new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(metadataJson, ProductWithVariantsRequest.class);
        List<MultipartFile> imageList = files != null ? List.of(files) : List.of();
        productService.updateProductWithVariants(productId, request, imageList, userInfo);
        return ResponseEntity.ok(new MessageResponse("Product updated successfully!", HttpStatus.OK));
    }

    @PostMapping(value = "import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<Map<String, Object>> importExcel(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {

        UserInfo userInfo = tokenUtils.getUserInfo(token.substring(7));
        try {
            Map<String, Object> result = excelImportService.importFromExcel(file, userInfo);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Excel import failed", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Import failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("category")
    public ResponseEntity<List<Category>> getCategory() {
        List<Category> categories = categoryService.getAllRootCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("category/{categoryId}/subcategories")
    public ResponseEntity<List<Category>> getSubCategories(@PathVariable("categoryId") Long categoryId) {
        List<Category> subCategories = categoryService.getSubCategories(categoryId);
        return ResponseEntity.ok(subCategories);
    }

    @PostMapping("/check-parent")
    public ResponseEntity<Map<String, Object>> checkParentProduct(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "category") String category,
            @RequestParam(name = "subCategory") String subCategory) {
        return ResponseEntity.ok(Map.of("message", "Check parent"));
    }

    @PostMapping("{productId}/variants")
    @Transactional
    public ResponseEntity<MessageResponse> addVariant(
            @PathVariable("productId") Long productId,
            @RequestPart("metadata") String metadataJson,
            @RequestPart(value = "file", required = false) MultipartFile[] images,
            @RequestHeader("Authorization") String token) throws IOException, ImageReadException {

        UserInfo userInfo = tokenUtils.getUserInfo(token.substring(7));
        AddVariantRequest request = new com.fasterxml.jackson.databind.ObjectMapper().readValue(metadataJson, AddVariantRequest.class);
        productService.addVariant(productId, request, images, userInfo);
        return ResponseEntity.ok(new MessageResponse("Variant added successfully!", HttpStatus.OK));
    }

    @PostMapping("variants/{variantId}")
    @Transactional
    public ResponseEntity<MessageResponse> updateVariant(
            @PathVariable("variantId") Long variantId,
            @RequestPart("metadata") String metadataJson,
            @RequestPart(value = "file", required = false) MultipartFile[] newImages,
            @RequestHeader("Authorization") String token) throws IOException, ImageReadException {

        UserInfo userInfo = tokenUtils.getUserInfo(token.substring(7));
        UpdateVariantRequest request = new com.fasterxml.jackson.databind.ObjectMapper().readValue(metadataJson, UpdateVariantRequest.class);
        productService.updateVariant(variantId, request, newImages, userInfo);
        return ResponseEntity.ok(new MessageResponse("Variant updated successfully!", HttpStatus.OK));
    }

    @PutMapping("{productId}")
    public ResponseEntity<MessageResponse> updateProduct(
            @PathVariable("productId") Long productId,
            @RequestBody UpdateProductRequest request,
            @RequestHeader("Authorization") String token) {

        UserInfo userInfo = tokenUtils.getUserInfo(token.substring(7));
        productService.updateProduct(productId, request, userInfo);
        return ResponseEntity.ok(new MessageResponse("Product updated successfully!", HttpStatus.OK));
    }

    @DeleteMapping("{productId}")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(new MessageResponse("Product deleted successfully!", HttpStatus.OK));
    }

    @DeleteMapping("variants/{variantId}")
    public ResponseEntity<MessageResponse> deleteVariant(@PathVariable("variantId") Long variantId) {
        productService.deleteVariant(variantId);
        return ResponseEntity.ok(new MessageResponse("Variant deleted successfully!", HttpStatus.OK));
    }
}
