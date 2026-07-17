package com.mindoot.onlinestore.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mindoot.onlinestore.enums.InventoryStatus;
import com.mindoot.onlinestore.exception.BadRequestException;
import com.mindoot.onlinestore.model.Category;
import com.mindoot.onlinestore.model.Inventory;
import com.mindoot.onlinestore.model.Product;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.repository.CategoryRepository;
import com.mindoot.onlinestore.repository.InventoryRepository;
import com.mindoot.onlinestore.repository.ProductRepository;
import com.mindoot.onlinestore.repository.ProductVariantRepository;
import com.mindoot.onlinestore.utility.UserInfo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExcelImportService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Map<String, Object> importFromExcel(MultipartFile file, UserInfo userInfo) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                result.put("success", false);
                result.put("message", "Excel file is empty");
                return result;
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String productName = getCellStringValue(row.getCell(0));
                    String brand = getCellStringValue(row.getCell(1));
                    String category = getCellStringValue(row.getCell(2));
                    String shortDesc = getCellStringValue(row.getCell(3));
                    String tags = getCellStringValue(row.getCell(4));
                    String variantName = getCellStringValue(row.getCell(5));
                    String weight = getCellStringValue(row.getCell(6));
                    String unit = getCellStringValue(row.getCell(7));
                    String sku = getCellStringValue(row.getCell(8));
                    String barcode = getCellStringValue(row.getCell(9));
                    Double retailPrice = getCellDoubleValue(row.getCell(10));
                    Double wholesalePrice = getCellDoubleValue(row.getCell(11));
                    Boolean wholesaleEnabled = getCellBooleanValue(row.getCell(12));
                    Integer minWholesaleQty = getCellIntValue(row.getCell(13));
                    Double wholesaleDiscount = getCellDoubleValue(row.getCell(14));
                    Integer stock = getCellIntValue(row.getCell(15));
                    Integer lowStockThreshold = getCellIntValue(row.getCell(16));
                    String longDesc = getCellStringValue(row.getCell(17));
                    String seoTitle = getCellStringValue(row.getCell(18));
                    String seoDescription = getCellStringValue(row.getCell(19));

                    if (productName == null || productName.isBlank()) {
                        errors.add("Row " + (i + 1) + ": Product name is required");
                        errorCount++;
                        continue;
                    }
                    if (retailPrice == null || retailPrice <= 0) {
                        errors.add("Row " + (i + 1) + ": Retail price is required and must be > 0");
                        errorCount++;
                        continue;
                    }

                    String slug = generateSlug(productName);
                    String productSku = (sku != null && !sku.isBlank()) ? sku : "PRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                    Long categoryId = null;
                    if (category != null && !category.isBlank()) {
                        var catOpt = categoryRepository.findByNameIgnoreCase(category.trim());
                        if (catOpt.isPresent()) {
                            categoryId = catOpt.get().getId();
                        }
                    }

                    Product product = null;
                    if (productSku != null) {
                        product = productRepository.findBySku(productSku).orElse(null);
                    }
                    if (product == null) {
                        List<Product> existingByName = productRepository.findByKeyword(productName.trim());
                        for (Product p : existingByName) {
                            if (p.getName().equalsIgnoreCase(productName.trim())) {
                                product = p;
                                break;
                            }
                        }
                    }

                    if (product == null) {
                        product = new Product();
                        product.setName(productName.trim());
                        product.setSlug(slug);
                        product.setProductId("PRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                        product.setSku(productSku);
                        product.setBrand(brand);
                        product.setShortDescription(shortDesc);
                        product.setLongDescription(longDesc);
                        product.setTags(tags);
                        product.setCategoryId(categoryId);
                        product.setSeoTitle(seoTitle);
                        product.setSeoDescription(seoDescription);
                        product.setActive(true);
                        product.setIsFeatured(false);
                        product.setSortOrder(0);
                        product.setUploadedBy(userInfo.getId());
                        product.setUploadedAt(LocalDateTime.now());

                        if (productRepository.existsBySlug(product.getSlug())) {
                            product.setSlug(product.getSlug() + "-" + UUID.randomUUID().toString().substring(0, 4));
                        }

                        product = productRepository.save(product);
                    }

                    String variantSku = (sku != null && !sku.isBlank()) ? sku : generateVariantSku(productName, weight, unit);
                    if (productVariantRepository.existsBySku(variantSku)) {
                        errors.add("Row " + (i + 1) + ": Variant SKU '" + variantSku + "' already exists, skipped");
                        errorCount++;
                        continue;
                    }

                    ProductVariant variant = new ProductVariant();
                    variant.setProduct(product);
                    variant.setVariantName(variantName != null ? variantName : (weight != null ? weight + " " + (unit != null ? unit : "") : "Default"));
                    variant.setWeight(weight);
                    variant.setUnit(unit);
                    variant.setSku(variantSku);
                    variant.setBarcode(barcode);
                    variant.setRetailPrice(retailPrice);
                    variant.setWholesalePrice(wholesalePrice != null ? wholesalePrice : 0.0);
                    variant.setWholesaleEnabled(wholesaleEnabled != null ? wholesaleEnabled : false);
                    variant.setMinWholesaleQuantity(minWholesaleQty);
                    variant.setWholesaleDiscount(wholesaleDiscount);
                    variant.setActive(true);
                    variant.setSortOrder(0);

                    ProductVariant savedVariant = productVariantRepository.save(variant);

                    Inventory inventory = new Inventory();
                    inventory.setVariant(savedVariant);
                    inventory.setAvailableQuantity(stock != null ? stock : 0);
                    inventory.setReservedQuantity(0);
                    inventory.setLowStockThreshold(lowStockThreshold != null ? lowStockThreshold : 5);
                    inventory.setInventoryStatus(InventoryStatus.IN_STOCK);
                    inventory.setLastUpdated(LocalDateTime.now());
                    inventoryRepository.save(inventory);

                    successCount++;
                } catch (Exception e) {
                    errors.add("Row " + (i + 1) + ": " + e.getMessage());
                    errorCount++;
                }
            }
        }

        result.put("success", errorCount == 0);
        result.put("message", successCount + " products imported successfully" + (errorCount > 0 ? ", " + errorCount + " errors" : ""));
        result.put("successCount", successCount);
        result.put("errorCount", errorCount);
        result.put("errors", errors);

        log.info("Excel import completed: {} success, {} errors", successCount, errorCount);
        return result;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);
        return (value != null && !value.isBlank()) ? value.trim() : null;
    }

    private Double getCellDoubleValue(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            }
            String str = cell.getStringCellValue();
            if (str != null && !str.isBlank()) {
                return Double.parseDouble(str.trim().replaceAll("[^\\d.]", ""));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private Integer getCellIntValue(Cell cell) {
        Double val = getCellDoubleValue(cell);
        return val != null ? val.intValue() : null;
    }

    private Boolean getCellBooleanValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        }
        String str = getCellStringValue(cell);
        if (str != null) {
            return str.equalsIgnoreCase("true") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("1");
        }
        return null;
    }

    private String generateSlug(String name) {
        if (name == null) return "";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private String generateVariantSku(String productName, String weight, String unit) {
        String prefix = (productName != null && productName.length() >= 3)
                ? productName.substring(0, 3).toUpperCase()
                : "PRD";
        String weightCode = weight != null ? weight.toUpperCase() : "0";
        String unitCode = unit != null ? unit.toUpperCase().substring(0, Math.min(unit.length(), 2)) : "GM";
        String uuidSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return String.format("%s-%s%s-%s", prefix, weightCode, unitCode, uuidSuffix);
    }
}
