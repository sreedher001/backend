package com.mindoot.onlinestore.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.imaging.ImageReadException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mindoot.onlinestore.dto.AddVariantRequest;
import com.mindoot.onlinestore.dto.ProductAutocompleteDto;
import com.mindoot.onlinestore.dto.ProductFilterDto;
import com.mindoot.onlinestore.dto.ProductResponseDto;
import com.mindoot.onlinestore.dto.ProductWithVariantsRequest;
import com.mindoot.onlinestore.dto.UpdateProductRequest;
import com.mindoot.onlinestore.dto.UpdateVariantRequest;
import com.mindoot.onlinestore.utility.UserInfo;

@Component
public interface ProductService {

    List<ProductResponseDto> getAllProducts();

    Page<ProductResponseDto> getAllProducts(int page, int size);

    Page<ProductResponseDto> searchProducts(String query, int page, int size);

    ProductResponseDto getProductById(Long id);

    ProductResponseDto getProductBySlug(String slug);

    List<ProductResponseDto> filterProducts(ProductFilterDto filterDto);

    void createProduct(String metadata, MultipartFile[] files, UserInfo userInfo) throws IOException, ImageReadException;

    void updateProduct(Long productId, UpdateProductRequest request, UserInfo userInfo);

    void addVariant(Long productId, AddVariantRequest request, MultipartFile[] images, UserInfo userInfo) throws IOException, ImageReadException;

    void updateVariant(Long variantId, UpdateVariantRequest request, MultipartFile[] newImages, UserInfo userInfo) throws IOException, ImageReadException;

    ProductResponseDto findByVariantId(Long variantId);

    List<ProductAutocompleteDto> autocompleteProducts(String query);

    ProductResponseDto getAllSimilarItems(Long variantId);

    void deleteProduct(Long productId);

    void deleteVariant(Long variantId);

    void createProductWithVariants(ProductWithVariantsRequest request, List<MultipartFile> variantImages, UserInfo userInfo) throws IOException;

    void updateProductWithVariants(Long productId, ProductWithVariantsRequest request, List<MultipartFile> newVariantImages, UserInfo userInfo) throws IOException;
}
