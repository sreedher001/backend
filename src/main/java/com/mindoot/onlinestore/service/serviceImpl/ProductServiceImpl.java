package com.mindoot.onlinestore.service.serviceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.imaging.ImageReadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindoot.onlinestore.dto.AddVariantRequest;
import com.mindoot.onlinestore.dto.ProductAutocompleteDto;
import com.mindoot.onlinestore.dto.ProductFilterDto;
import com.mindoot.onlinestore.dto.ProductImageDto;
import com.mindoot.onlinestore.dto.ProductResponseDto;
import com.mindoot.onlinestore.dto.ProductVariantResponseDto;
import com.mindoot.onlinestore.dto.UpdateProductRequest;
import com.mindoot.onlinestore.dto.UpdateVariantRequest;
import com.mindoot.onlinestore.dto.ProductWithVariantsRequest;
import com.mindoot.onlinestore.dto.VariantDto;
import com.mindoot.onlinestore.enums.InventoryStatus;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.exception.BadRequestException;
import com.mindoot.onlinestore.model.Category;
import com.mindoot.onlinestore.model.Inventory;
import com.mindoot.onlinestore.model.Product;
import com.mindoot.onlinestore.model.ProductImage;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.model.VariantRatingSummary;
import com.mindoot.onlinestore.repository.CategoryRepository;
import com.mindoot.onlinestore.repository.InventoryRepository;
import com.mindoot.onlinestore.repository.ProductImageRepository;
import com.mindoot.onlinestore.repository.ProductRepository;
import com.mindoot.onlinestore.repository.ProductVariantRepository;
import com.mindoot.onlinestore.repository.VariantRatingSummaryRepository;
import com.mindoot.onlinestore.service.LocalFileStorageService;
import com.mindoot.onlinestore.service.ProductService;
import com.mindoot.onlinestore.utility.UserInfo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private VariantRatingSummaryRepository variantRatingSummaryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocalFileStorageService fileStorageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductResponseDto> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadedAt").descending());
        return productRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public Page<ProductResponseDto> searchProducts(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadedAt").descending());
        Page<Product> products;
        try {
            products = productRepository.searchFullText(query, pageable);
            if (products == null || products.isEmpty()) {
                products = productRepository.searchFallback(query, pageable);
            }
        } catch (Exception e) {
            log.warn("Full text search failed, falling back to like search: {}", e.getMessage());
            products = productRepository.searchFallback(query, pageable);
        }
        return products.map(this::mapToDto);
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Product not found with ID: " + id, HttpStatus.BAD_REQUEST));
        return mapToDto(product);
    }

    @Override
    public ProductResponseDto getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ApplicationException("Product not found with slug: " + slug, HttpStatus.BAD_REQUEST));
        return mapToDto(product);
    }

    @Override
    public List<ProductResponseDto> filterProducts(ProductFilterDto filterDto) {
        List<Product> products = productRepository.findByActiveTrue();

        return products.stream()
                .filter(p -> filterDto.getCategoryId() == null || p.getCategoryId().equals(filterDto.getCategoryId()))
                .filter(p -> filterDto.getSubCategoryId() == null || p.getSubCategoryId().equals(filterDto.getSubCategoryId()))
                .filter(p -> filterDto.getBrand() == null || filterDto.getBrand().equalsIgnoreCase(p.getBrand()))
                .filter(p -> filterDto.getIsFeatured() == null || p.getIsFeatured().equals(filterDto.getIsFeatured()))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void createProduct(String metadata, MultipartFile[] files, UserInfo userInfo) throws IOException, ImageReadException {
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> metadataMap = objectMapper.readValue(metadata, objectMapper.getTypeFactory().constructMapType(java.util.Map.class, String.class, Object.class));

        Product product = new Product();
        product.setName((String) metadataMap.get("name"));
        product.setSlug(generateSlug((String) metadataMap.get("name")));
        product.setShortDescription((String) metadataMap.get("shortDescription"));
        product.setLongDescription((String) metadataMap.get("longDescription"));
        product.setBrand((String) metadataMap.get("brand"));
        product.setSku((String) metadataMap.get("sku"));
        product.setBarcode((String) metadataMap.get("barcode"));
        product.setActive(metadataMap.get("active") != null ? (Boolean) metadataMap.get("active") : true);
        product.setIsFeatured(metadataMap.get("isFeatured") != null ? (Boolean) metadataMap.get("isFeatured") : false);
        product.setSeoTitle((String) metadataMap.get("seoTitle"));
        product.setSeoDescription((String) metadataMap.get("seoDescription"));
        product.setTags((String) metadataMap.get("tags"));
        product.setSortOrder(metadataMap.get("sortOrder") != null ? ((Number) metadataMap.get("sortOrder")).intValue() : 0);
        product.setUploadedBy(userInfo.getId());
        product.setUploadedAt(LocalDateTime.now());
        product.setProductId("PRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        if (metadataMap.get("categoryId") != null) {
            product.setCategoryId(((Number) metadataMap.get("categoryId")).longValue());
        }
        if (metadataMap.get("subCategoryId") != null) {
            product.setSubCategoryId(((Number) metadataMap.get("subCategoryId")).longValue());
        }

        // Check unique constraints
        if (productRepository.existsBySku(product.getSku())) {
            throw new BadRequestException("Product with SKU '" + product.getSku() + "' already exists");
        }
        if (productRepository.existsBySlug(product.getSlug())) {
            product.setSlug(product.getSlug() + "-" + UUID.randomUUID().toString().substring(0, 4));
        }

        Product savedProduct = productRepository.save(product);

        // Create variant from metadata
        ProductVariant variant = new ProductVariant();
        variant.setProduct(savedProduct);
        variant.setWeight((String) metadataMap.get("weight"));
        variant.setUnit((String) metadataMap.get("unit"));
        variant.setVariantName((String) metadataMap.get("variantName"));
        variant.setSku((String) metadataMap.get("variantSku"));
        variant.setBarcode((String) metadataMap.get("variantBarcode"));
        variant.setRetailPrice(metadataMap.get("retailPrice") != null ? ((Number) metadataMap.get("retailPrice")).doubleValue() : 0.0);
        variant.setWholesalePrice(metadataMap.get("wholesalePrice") != null ? ((Number) metadataMap.get("wholesalePrice")).doubleValue() : 0.0);
        variant.setWholesaleEnabled(metadataMap.get("wholesaleEnabled") != null ? (Boolean) metadataMap.get("wholesaleEnabled") : false);
        variant.setMinWholesaleQuantity(metadataMap.get("minWholesaleQuantity") != null ? ((Number) metadataMap.get("minWholesaleQuantity")).intValue() : null);
        variant.setWholesaleDiscount(metadataMap.get("wholesaleDiscount") != null ? ((Number) metadataMap.get("wholesaleDiscount")).doubleValue() : null);
        variant.setActive(true);
        variant.setSortOrder(0);
        variant.setSku(variant.getSku() != null ? variant.getSku() : generateSku(savedProduct.getName(), variant.getWeight(), variant.getUnit()));

        ProductVariant savedVariant = productVariantRepository.save(variant);

        // Create inventory
        Inventory inventory = new Inventory();
        inventory.setVariant(savedVariant);
        inventory.setAvailableQuantity(metadataMap.get("availableQuantity") != null ? ((Number) metadataMap.get("availableQuantity")).intValue() : 0);
        inventory.setReservedQuantity(0);
        inventory.setLowStockThreshold(metadataMap.get("lowStockThreshold") != null ? ((Number) metadataMap.get("lowStockThreshold")).intValue() : 5);
        inventory.setInventoryStatus(InventoryStatus.IN_STOCK);
        inventory.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inventory);

        // Upload images
        if (files != null && files.length > 0) {
            String folderPath = "products/" + savedProduct.getId() + "/" + savedVariant.getId();
            List<String> imagePaths = fileStorageService.uploadImages(files, folderPath);

            for (int i = 0; i < imagePaths.size(); i++) {
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(imagePaths.get(i));
                productImage.setVariant(savedVariant);
                productImage.setProduct(savedProduct);
                productImage.setIsThumbnail(i == 0);
                productImage.setSortOrder(i);
                productImage.setUploadedAt(LocalDateTime.now());
                productImage.setViewType(i == 0 ? "front" : "gallery");
                productImageRepository.save(productImage);
            }
        }

        log.info("Product created: {} with variant: {}", savedProduct.getId(), savedVariant.getId());
    }

    @Override
    public void updateProduct(Long productId, UpdateProductRequest request, UserInfo userInfo) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApplicationException("Product not found", HttpStatus.BAD_REQUEST));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getSlug() != null) product.setSlug(request.getSlug());
        if (request.getShortDescription() != null) product.setShortDescription(request.getShortDescription());
        if (request.getLongDescription() != null) product.setLongDescription(request.getLongDescription());
        if (request.getCategoryId() != null) product.setCategoryId(request.getCategoryId());
        if (request.getSubCategoryId() != null) product.setSubCategoryId(request.getSubCategoryId());
        if (request.getBrand() != null) product.setBrand(request.getBrand());
        if (request.getSku() != null) product.setSku(request.getSku());
        if (request.getBarcode() != null) product.setBarcode(request.getBarcode());
        if (request.getActive() != null) product.setActive(request.getActive());
        if (request.getIsFeatured() != null) product.setIsFeatured(request.getIsFeatured());
        if (request.getSeoTitle() != null) product.setSeoTitle(request.getSeoTitle());
        if (request.getSeoDescription() != null) product.setSeoDescription(request.getSeoDescription());
        if (request.getTags() != null) product.setTags(request.getTags());
        if (request.getSortOrder() != null) product.setSortOrder(request.getSortOrder());

        productRepository.save(product);
    }

    @Override
    public void addVariant(Long productId, AddVariantRequest request, MultipartFile[] images, UserInfo userInfo) throws IOException, ImageReadException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApplicationException("Product not found", HttpStatus.BAD_REQUEST));

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setWeight(request.getWeight());
        variant.setUnit(request.getUnit());
        variant.setVariantName(request.getVariantName());
        variant.setSku(request.getSku() != null ? request.getSku() : generateSku(product.getName(), request.getWeight(), request.getUnit()));
        variant.setBarcode(request.getBarcode());
        variant.setRetailPrice(request.getRetailPrice());
        variant.setWholesalePrice(request.getWholesalePrice());
        variant.setWholesaleEnabled(request.getWholesaleEnabled());
        variant.setMinWholesaleQuantity(request.getMinWholesaleQuantity());
        variant.setWholesaleDiscount(request.getWholesaleDiscount());
        variant.setActive(request.getActive() != null ? request.getActive() : true);
        variant.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        if (productVariantRepository.existsBySku(variant.getSku())) {
            throw new BadRequestException("Variant with SKU '" + variant.getSku() + "' already exists");
        }

        ProductVariant savedVariant = productVariantRepository.save(variant);

        // Create inventory
        Inventory inventory = new Inventory();
        inventory.setVariant(savedVariant);
        inventory.setAvailableQuantity(request.getAvailableQuantity() != null ? request.getAvailableQuantity() : 0);
        inventory.setReservedQuantity(0);
        inventory.setLowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 5);
        inventory.setInventoryStatus(InventoryStatus.IN_STOCK);
        inventory.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inventory);

        // Upload images
        if (images != null && images.length > 0) {
            String folderPath = "products/" + product.getId() + "/" + savedVariant.getId();
            List<String> imagePaths = fileStorageService.uploadImages(images, folderPath);

            for (int i = 0; i < imagePaths.size(); i++) {
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(imagePaths.get(i));
                productImage.setVariant(savedVariant);
                productImage.setProduct(product);
                productImage.setIsThumbnail(i == 0);
                productImage.setSortOrder(i);
                productImage.setUploadedAt(LocalDateTime.now());
                productImage.setViewType(i == 0 ? "front" : "gallery");
                productImageRepository.save(productImage);
            }
        }

        log.info("Variant added to product: {} variant: {}", productId, savedVariant.getId());
    }

    @Override
    public void updateVariant(Long variantId, UpdateVariantRequest request, MultipartFile[] newImages, UserInfo userInfo) throws IOException, ImageReadException {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ApplicationException("Variant not found", HttpStatus.BAD_REQUEST));

        if (request.getWeight() != null) variant.setWeight(request.getWeight());
        if (request.getUnit() != null) variant.setUnit(request.getUnit());
        if (request.getVariantName() != null) variant.setVariantName(request.getVariantName());
        if (request.getSku() != null) variant.setSku(request.getSku());
        if (request.getBarcode() != null) variant.setBarcode(request.getBarcode());
        if (request.getRetailPrice() != null) variant.setRetailPrice(request.getRetailPrice());
        if (request.getWholesalePrice() != null) variant.setWholesalePrice(request.getWholesalePrice());
        if (request.getWholesaleEnabled() != null) variant.setWholesaleEnabled(request.getWholesaleEnabled());
        if (request.getMinWholesaleQuantity() != null) variant.setMinWholesaleQuantity(request.getMinWholesaleQuantity());
        if (request.getWholesaleDiscount() != null) variant.setWholesaleDiscount(request.getWholesaleDiscount());
        if (request.getActive() != null) variant.setActive(request.getActive());
        if (request.getSortOrder() != null) variant.setSortOrder(request.getSortOrder());

        // Handle image removals
        if (request.getImagesToRemove() != null && !request.getImagesToRemove().isEmpty()) {
            List<String> pathsToRemove = new ArrayList<>();
            for (Long imageId : request.getImagesToRemove()) {
                ProductImage img = productImageRepository.findById(imageId).orElse(null);
                if (img != null) {
                    pathsToRemove.add(img.getImageUrl());
                    productImageRepository.deleteById(imageId);
                }
            }
            fileStorageService.deleteImages(pathsToRemove);
        }

        // Upload new images
        if (newImages != null && newImages.length > 0) {
            String folderPath = "products/" + variant.getProduct().getId() + "/" + variant.getId();
            List<String> imagePaths = fileStorageService.uploadImages(newImages, folderPath);

            for (String imagePath : imagePaths) {
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(imagePath);
                productImage.setVariant(variant);
                productImage.setProduct(variant.getProduct());
                productImage.setIsThumbnail(false);
                productImage.setSortOrder(variant.getImages().size());
                productImage.setUploadedAt(LocalDateTime.now());
                productImage.setViewType("gallery");
                productImageRepository.save(productImage);
            }
        }

        // Handle thumbnail selection
        if (request.getFrontImageId() != null) {
            variant.getImages().forEach(img -> img.setIsThumbnail(false));
            ProductImage thumbnail = productImageRepository.findById(request.getFrontImageId()).orElse(null);
            if (thumbnail != null) {
                thumbnail.setIsThumbnail(true);
                thumbnail.setViewType("front");
                productImageRepository.save(thumbnail);
            }
        }

        // Update inventory
        if (request.getAvailableQuantity() != null || request.getLowStockThreshold() != null) {
            Inventory inventory = inventoryRepository.findByVariantId(variantId)
                    .orElse(new Inventory());
            inventory.setVariant(variant);
            if (request.getAvailableQuantity() != null) inventory.setAvailableQuantity(request.getAvailableQuantity());
            if (request.getLowStockThreshold() != null) inventory.setLowStockThreshold(request.getLowStockThreshold());
            inventory.setInventoryStatus(InventoryServiceImpl.determineStatus(inventory.getAvailableQuantity()));
            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);
        }

        productVariantRepository.save(variant);
    }

    @Override
    public ProductResponseDto findByVariantId(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ApplicationException("Variant not found", HttpStatus.NOT_FOUND));
        return mapToDto(variant.getProduct());
    }

    @Override
    public List<ProductAutocompleteDto> autocompleteProducts(String query) {
        List<Object[]> results = productRepository.searchForAutocomplete(query);
        return results.stream()
                .map(record -> new ProductAutocompleteDto(
                        ((Number) record[0]).longValue(),
                        (String) record[1],
                        (String) record[2],
                        null,
                        (String) record[3],
                        (String) record[4],
                        (String) record[5],
                        (String) record[6],
                        (String) record[7]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto getAllSimilarItems(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ApplicationException("Variant not found", HttpStatus.NOT_FOUND));
        return mapToDto(variant.getProduct());
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApplicationException("Product not found", HttpStatus.BAD_REQUEST));
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    public void deleteVariant(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ApplicationException("Variant not found", HttpStatus.BAD_REQUEST));
        variant.setActive(false);
        productVariantRepository.save(variant);
    }

    private ProductResponseDto mapToDto(Product product) {
        List<ProductVariantResponseDto> variantDtos = product.getVariants().stream()
                .filter(v -> v.getActive() != null && v.getActive())
                .map(this::mapVariantToDto)
                .collect(Collectors.toList());

        String categoryName = "";
        if (product.getCategoryId() != null) {
            Category cat = categoryRepository.findById(product.getCategoryId()).orElse(null);
            if (cat != null) categoryName = cat.getName();
        }

        String subCategoryName = "";
        if (product.getSubCategoryId() != null) {
            Category subCat = categoryRepository.findById(product.getSubCategoryId()).orElse(null);
            if (subCat != null) subCategoryName = subCat.getName();
        }

        List<ProductImageDto> productImages = product.getProductImages() != null ?
                product.getProductImages().stream()
                        .map(img -> ProductImageDto.builder()
                                .id(img.getId())
                                .imageUrl(img.getImageUrl())
                                .viewType(img.getViewType())
                                .isThumbnail(img.getIsThumbnail())
                                .sortOrder(img.getSortOrder())
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>();

        return ProductResponseDto.builder()
                .id(product.getId())
                .productId(product.getProductId())
                .name(product.getName())
                .slug(product.getSlug())
                .shortDescription(product.getShortDescription())
                .longDescription(product.getLongDescription())
                .categoryId(product.getCategoryId())
                .categoryName(categoryName)
                .subCategoryId(product.getSubCategoryId())
                .subCategoryName(subCategoryName)
                .brand(product.getBrand())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .active(product.getActive())
                .isFeatured(product.getIsFeatured())
                .thumbnail(product.getThumbnail())
                .seoTitle(product.getSeoTitle())
                .seoDescription(product.getSeoDescription())
                .tags(product.getTags())
                .sortOrder(product.getSortOrder())
                .rating(product.getRating())
                .uploadedAt(product.getUploadedAt())
                .variants(variantDtos)
                .productImages(productImages)
                .build();
    }

    private ProductVariantResponseDto mapVariantToDto(ProductVariant variant) {
        VariantRatingSummary summary = variantRatingSummaryRepository.findByVariantId(variant.getId()).orElse(null);

        List<ProductImageDto> images = variant.getImages() != null ?
                variant.getImages().stream()
                        .map(img -> ProductImageDto.builder()
                                .id(img.getId())
                                .imageUrl(img.getImageUrl())
                                .viewType(img.getViewType())
                                .isThumbnail(img.getIsThumbnail())
                                .sortOrder(img.getSortOrder())
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>();

        Integer availableQuantity = 0;
        String inventoryStatus = "UNKNOWN";
        if (variant.getInventory() != null) {
            availableQuantity = variant.getInventory().getAvailableQuantity();
            inventoryStatus = variant.getInventory().getInventoryStatus() != null ?
                    variant.getInventory().getInventoryStatus().name() : "UNKNOWN";
        }

        return ProductVariantResponseDto.builder()
                .id(variant.getId())
                .variantName(variant.getVariantName())
                .weight(variant.getWeight())
                .unit(variant.getUnit())
                .sku(variant.getSku())
                .barcode(variant.getBarcode())
                .retailPrice(variant.getRetailPrice())
                .wholesalePrice(variant.getWholesalePrice())
                .wholesaleEnabled(variant.getWholesaleEnabled())
                .minWholesaleQuantity(variant.getMinWholesaleQuantity())
                .wholesaleDiscount(variant.getWholesaleDiscount())
                .active(variant.getActive())
                .sortOrder(variant.getSortOrder())
                .imageUrl(variant.getImageUrl())
                .isFeatured(variant.getIsFeatured())
                .rating(summary != null ? summary.getAverageRating() : (variant.getRating() != null ? variant.getRating() : 0.0))
                .totalReviews(summary != null ? summary.getTotalReviews() : 0)
                .productImage(images)
                .availableQuantity(availableQuantity)
                .inventoryStatus(inventoryStatus)
                .build();
    }

    private String generateSku(String productName, String weight, String unit) {
        String prefix = (productName != null && productName.length() >= 3)
                ? productName.substring(0, 3).toUpperCase()
                : "PRD";
        String weightCode = weight != null ? weight.toUpperCase() : "0";
        String unitCode = unit != null ? unit.toUpperCase().substring(0, Math.min(unit.length(), 2)) : "GM";
        String uuidSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return String.format("%s-%s%s-%s", prefix, weightCode, unitCode, uuidSuffix);
    }

    private String generateSlug(String name) {
        if (name == null) return "";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    @Override
    public void createProductWithVariants(ProductWithVariantsRequest request, List<MultipartFile> variantImages, UserInfo userInfo) throws IOException {
        Product product = new Product();
        product.setName(request.getName());
        product.setSlug(generateSlug(request.getName()));
        product.setShortDescription(request.getShortDescription());
        product.setLongDescription(request.getLongDescription());
        product.setBrand(request.getBrand());
        product.setSku(request.getSku());
        product.setBarcode(request.getBarcode());
        product.setActive(true);
        product.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        product.setSeoTitle(request.getSeoTitle());
        product.setSeoDescription(request.getSeoDescription());
        product.setTags(request.getTags());
        product.setSortOrder(0);
        product.setUploadedBy(userInfo.getId());
        product.setUploadedAt(LocalDateTime.now());
        product.setProductId("PRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        if (request.getCategoryId() != null) {
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getSubCategoryId() != null) {
            product.setSubCategoryId(request.getSubCategoryId());
        }

        if (productRepository.existsBySku(product.getSku())) {
            throw new BadRequestException("Product with SKU '" + product.getSku() + "' already exists");
        }
        if (productRepository.existsBySlug(product.getSlug())) {
            product.setSlug(product.getSlug() + "-" + UUID.randomUUID().toString().substring(0, 4));
        }

        Product savedProduct = productRepository.save(product);

        if (request.getVariants() != null) {
            int imageIndex = 0;
            for (int i = 0; i < request.getVariants().size(); i++) {
                VariantDto vd = request.getVariants().get(i);

                ProductVariant variant = new ProductVariant();
                variant.setProduct(savedProduct);
                variant.setWeight(vd.getWeight());
                variant.setUnit(vd.getUnit());
                variant.setVariantName(vd.getVariantName());
                variant.setBarcode(vd.getBarcode());
                variant.setRetailPrice(vd.getRetailPrice() != null ? vd.getRetailPrice() : 0.0);
                variant.setWholesalePrice(vd.getWholesalePrice() != null ? vd.getWholesalePrice() : 0.0);
                variant.setWholesaleEnabled(vd.getWholesaleEnabled() != null ? vd.getWholesaleEnabled() : false);
                variant.setMinWholesaleQuantity(vd.getMinWholesaleQuantity());
                variant.setWholesaleDiscount(vd.getWholesaleDiscount());
                variant.setActive(true);
                variant.setSortOrder(vd.getSortOrder() != null ? vd.getSortOrder() : i);

                String variantSku = vd.getSku() != null && !vd.getSku().isBlank()
                        ? vd.getSku()
                        : generateSku(savedProduct.getName(), vd.getWeight(), vd.getUnit());
                variant.setSku(variantSku);

                if (productVariantRepository.existsBySku(variantSku)) {
                    throw new BadRequestException("Variant with SKU '" + variantSku + "' already exists");
                }

                ProductVariant savedVariant = productVariantRepository.save(variant);

                Inventory inventory = new Inventory();
                inventory.setVariant(savedVariant);
                inventory.setAvailableQuantity(vd.getAvailableQuantity() != null ? vd.getAvailableQuantity() : 0);
                inventory.setReservedQuantity(0);
                inventory.setLowStockThreshold(vd.getLowStockThreshold() != null ? vd.getLowStockThreshold() : 5);
                inventory.setInventoryStatus(InventoryStatus.IN_STOCK);
                inventory.setLastUpdated(LocalDateTime.now());
                inventoryRepository.save(inventory);

                String folderPath = "products/" + savedProduct.getId() + "/" + savedVariant.getId();
                List<MultipartFile> variantImageFiles = getVariantImages(variantImages, i);
                if (!variantImageFiles.isEmpty()) {
                    MultipartFile[] arr = variantImageFiles.toArray(new MultipartFile[0]);
                    List<String> imagePaths = fileStorageService.uploadImages(arr, folderPath);
                    for (int j = 0; j < imagePaths.size(); j++) {
                        ProductImage productImage = new ProductImage();
                        productImage.setImageUrl(imagePaths.get(j));
                        productImage.setVariant(savedVariant);
                        productImage.setProduct(savedProduct);
                        productImage.setIsThumbnail(j == 0);
                        productImage.setSortOrder(j);
                        productImage.setUploadedAt(LocalDateTime.now());
                        productImage.setViewType(j == 0 ? "front" : "gallery");
                        productImageRepository.save(productImage);
                    }
                }

                imageIndex += variantImageFiles.size();
            }
        }

        log.info("Product created with variants: {}", savedProduct.getId());
    }

    @Override
    public void updateProductWithVariants(Long productId, ProductWithVariantsRequest request, List<MultipartFile> newVariantImages, UserInfo userInfo) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApplicationException("Product not found", HttpStatus.BAD_REQUEST));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getShortDescription() != null) product.setShortDescription(request.getShortDescription());
        if (request.getLongDescription() != null) product.setLongDescription(request.getLongDescription());
        if (request.getCategoryId() != null) product.setCategoryId(request.getCategoryId());
        if (request.getSubCategoryId() != null) product.setSubCategoryId(request.getSubCategoryId());
        if (request.getBrand() != null) product.setBrand(request.getBrand());
        if (request.getSku() != null) product.setSku(request.getSku());
        if (request.getBarcode() != null) product.setBarcode(request.getBarcode());
        if (request.getIsFeatured() != null) product.setIsFeatured(request.getIsFeatured());
        if (request.getSeoTitle() != null) product.setSeoTitle(request.getSeoTitle());
        if (request.getSeoDescription() != null) product.setSeoDescription(request.getSeoDescription());
        if (request.getTags() != null) product.setTags(request.getTags());

        productRepository.save(product);

        if (request.getVariants() != null) {
            List<Long> incomingVariantIds = new ArrayList<>();
            for (VariantDto vd : request.getVariants()) {
                if (vd.getId() != null) {
                    incomingVariantIds.add(vd.getId());
                }
            }

            List<ProductVariant> existingVariants = productVariantRepository.findByProductId(productId);
            for (ProductVariant existing : existingVariants) {
                if (!incomingVariantIds.contains(existing.getId())) {
                    existing.setActive(false);
                    productVariantRepository.save(existing);
                }
            }

            int imageIndex = 0;
            for (int i = 0; i < request.getVariants().size(); i++) {
                VariantDto vd = request.getVariants().get(i);

                if (vd.getId() != null) {
                    ProductVariant variant = productVariantRepository.findById(vd.getId())
                            .orElseThrow(() -> new ApplicationException("Variant not found: " + vd.getId(), HttpStatus.BAD_REQUEST));

                    if (vd.getWeight() != null) variant.setWeight(vd.getWeight());
                    if (vd.getUnit() != null) variant.setUnit(vd.getUnit());
                    if (vd.getVariantName() != null) variant.setVariantName(vd.getVariantName());
                    if (vd.getSku() != null) variant.setSku(vd.getSku());
                    if (vd.getBarcode() != null) variant.setBarcode(vd.getBarcode());
                    if (vd.getRetailPrice() != null) variant.setRetailPrice(vd.getRetailPrice());
                    if (vd.getWholesalePrice() != null) variant.setWholesalePrice(vd.getWholesalePrice());
                    if (vd.getWholesaleEnabled() != null) variant.setWholesaleEnabled(vd.getWholesaleEnabled());
                    if (vd.getMinWholesaleQuantity() != null) variant.setMinWholesaleQuantity(vd.getMinWholesaleQuantity());
                    if (vd.getWholesaleDiscount() != null) variant.setWholesaleDiscount(vd.getWholesaleDiscount());
                    if (vd.getSortOrder() != null) variant.setSortOrder(vd.getSortOrder());
                    variant.setActive(true);

                    productVariantRepository.save(variant);

                    if (vd.getAvailableQuantity() != null || vd.getLowStockThreshold() != null) {
                        Inventory inventory = inventoryRepository.findByVariantId(variant.getId())
                                .orElse(new Inventory());
                        inventory.setVariant(variant);
                        if (vd.getAvailableQuantity() != null) inventory.setAvailableQuantity(vd.getAvailableQuantity());
                        if (vd.getLowStockThreshold() != null) inventory.setLowStockThreshold(vd.getLowStockThreshold());
                        inventory.setInventoryStatus(InventoryServiceImpl.determineStatus(inventory.getAvailableQuantity()));
                        inventory.setLastUpdated(LocalDateTime.now());
                        inventoryRepository.save(inventory);
                    }

                    List<MultipartFile> variantImageFiles = getVariantImages(newVariantImages, imageIndex);
                    if (!variantImageFiles.isEmpty()) {
                        String folderPath = "products/" + productId + "/" + variant.getId();
                        MultipartFile[] arr = variantImageFiles.toArray(new MultipartFile[0]);
                        List<String> imagePaths = fileStorageService.uploadImages(arr, folderPath);
                        for (String imagePath : imagePaths) {
                            ProductImage productImage = new ProductImage();
                            productImage.setImageUrl(imagePath);
                            productImage.setVariant(variant);
                            productImage.setProduct(product);
                            productImage.setIsThumbnail(false);
                            productImage.setSortOrder(variant.getImages().size());
                            productImage.setUploadedAt(LocalDateTime.now());
                            productImage.setViewType("gallery");
                            productImageRepository.save(productImage);
                        }
                    }
                    imageIndex += variantImageFiles.size();
                } else {
                    ProductVariant variant = new ProductVariant();
                    variant.setProduct(product);
                    variant.setWeight(vd.getWeight());
                    variant.setUnit(vd.getUnit());
                    variant.setVariantName(vd.getVariantName());
                    variant.setBarcode(vd.getBarcode());
                    variant.setRetailPrice(vd.getRetailPrice() != null ? vd.getRetailPrice() : 0.0);
                    variant.setWholesalePrice(vd.getWholesalePrice() != null ? vd.getWholesalePrice() : 0.0);
                    variant.setWholesaleEnabled(vd.getWholesaleEnabled() != null ? vd.getWholesaleEnabled() : false);
                    variant.setMinWholesaleQuantity(vd.getMinWholesaleQuantity());
                    variant.setWholesaleDiscount(vd.getWholesaleDiscount());
                    variant.setActive(true);
                    variant.setSortOrder(vd.getSortOrder() != null ? vd.getSortOrder() : i);

                    String variantSku = vd.getSku() != null && !vd.getSku().isBlank()
                            ? vd.getSku()
                            : generateSku(product.getName(), vd.getWeight(), vd.getUnit());
                    variant.setSku(variantSku);

                    if (productVariantRepository.existsBySku(variantSku)) {
                        throw new BadRequestException("Variant with SKU '" + variantSku + "' already exists");
                    }

                    ProductVariant savedVariant = productVariantRepository.save(variant);

                    Inventory inventory = new Inventory();
                    inventory.setVariant(savedVariant);
                    inventory.setAvailableQuantity(vd.getAvailableQuantity() != null ? vd.getAvailableQuantity() : 0);
                    inventory.setReservedQuantity(0);
                    inventory.setLowStockThreshold(vd.getLowStockThreshold() != null ? vd.getLowStockThreshold() : 5);
                    inventory.setInventoryStatus(InventoryStatus.IN_STOCK);
                    inventory.setLastUpdated(LocalDateTime.now());
                    inventoryRepository.save(inventory);

                    List<MultipartFile> variantImageFiles = getVariantImages(newVariantImages, imageIndex);
                    if (!variantImageFiles.isEmpty()) {
                        String folderPath = "products/" + productId + "/" + savedVariant.getId();
                        MultipartFile[] arr = variantImageFiles.toArray(new MultipartFile[0]);
                        List<String> imagePaths = fileStorageService.uploadImages(arr, folderPath);
                        for (int j = 0; j < imagePaths.size(); j++) {
                            ProductImage productImage = new ProductImage();
                            productImage.setImageUrl(imagePaths.get(j));
                            productImage.setVariant(savedVariant);
                            productImage.setProduct(product);
                            productImage.setIsThumbnail(j == 0);
                            productImage.setSortOrder(j);
                            productImage.setUploadedAt(LocalDateTime.now());
                            productImage.setViewType(j == 0 ? "front" : "gallery");
                            productImageRepository.save(productImage);
                        }
                    }
                    imageIndex += variantImageFiles.size();
                }
            }
        }

        log.info("Product updated with variants: {}", productId);
    }

    private List<MultipartFile> getVariantImages(List<MultipartFile> allImages, int variantIndex) {
        if (allImages == null || allImages.isEmpty()) return new ArrayList<>();
        String prefix = "variant_" + variantIndex + "_";
        return allImages.stream()
                .filter(f -> f.getOriginalFilename() != null && f.getOriginalFilename().startsWith(prefix))
                .collect(Collectors.toList());
    }
}
