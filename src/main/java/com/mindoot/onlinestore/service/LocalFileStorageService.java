package com.mindoot.onlinestore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mindoot.onlinestore.model.Product;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.utility.UserInfo;

@Service
public interface LocalFileStorageService {

	List<String> uploadImages(MultipartFile[] files, String folderPath) throws IOException;

	String uploadImage(MultipartFile file, String folderPath) throws IOException;

	String getProductImagePath(Long productId, Long variantId, String fileName);

	String getBannerImagePath(String fileName);

	String getInvoicePath(String fileName);

	void deleteFile(String relativePath);

	void deleteImages(List<String> imagePaths);

	byte[] getImage(String relativePath);

	String getUploadBasePath();
}
