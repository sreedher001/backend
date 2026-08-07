package com.mindoot.onlinestore.service.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mindoot.onlinestore.exception.BadRequestException;
import com.mindoot.onlinestore.service.LocalFileStorageService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LocalFileStorageServiceImpl implements LocalFileStorageService {

	private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
	private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");

	@Value("${app.upload.dir:uploads}")
	private String uploadBasePath;

	@PostConstruct
	public void init() {
		try {
			Files.createDirectories(Paths.get(uploadBasePath));
			Files.createDirectories(Paths.get(uploadBasePath, "products"));
			Files.createDirectories(Paths.get(uploadBasePath, "banners"));
			Files.createDirectories(Paths.get(uploadBasePath, "invoices"));
		} catch (IOException e) {
			log.error("Could not create upload directories", e);
		}
	}

	private void validateImageFile(MultipartFile file) {
		String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
		String extension = "";
		int dotIndex = originalFilename.lastIndexOf('.');
		if (dotIndex > 0) {
			extension = originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT);
		}

		String contentType = file.getContentType() != null ? file.getContentType().toLowerCase(Locale.ROOT) : "";

		if (!ALLOWED_EXTENSIONS.contains(extension) || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
			throw new BadRequestException("Only JPG, PNG, and WEBP images are allowed");
		}
	}

	@Override
	public List<String> uploadImages(MultipartFile[] files, String folderPath) throws IOException {
		List<String> uploadedPaths = new ArrayList<>();
		Path targetDir = Paths.get(uploadBasePath, folderPath);
		Files.createDirectories(targetDir);

		for (MultipartFile file : files) {
			if (file.isEmpty()) continue;

			validateImageFile(file);

			String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
			String extension = "";
			int dotIndex = originalFilename.lastIndexOf('.');
			if (dotIndex > 0) {
				extension = originalFilename.substring(dotIndex);
			}

			String fileName = UUID.randomUUID().toString() + extension;
			Path targetPath = targetDir.resolve(fileName);

			Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

			String relativePath = folderPath + "/" + fileName;
			uploadedPaths.add(relativePath);
			log.info("Uploaded file: {}", relativePath);
		}

		return uploadedPaths;
	}

	@Override
	public String uploadImage(MultipartFile file, String folderPath) throws IOException {
		if (file == null || file.isEmpty()) return null;

		validateImageFile(file);

		Path targetDir = Paths.get(uploadBasePath, folderPath);
		Files.createDirectories(targetDir);

		String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
		String extension = "";
		int dotIndex = originalFilename.lastIndexOf('.');
		if (dotIndex > 0) {
			extension = originalFilename.substring(dotIndex);
		}

		String fileName = UUID.randomUUID().toString() + extension;
		Path targetPath = targetDir.resolve(fileName);

		Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

		String relativePath = folderPath + "/" + fileName;
		log.info("Uploaded file: {}", relativePath);
		return relativePath;
	}

	@Override
	public String getProductImagePath(Long productId, Long variantId, String fileName) {
		return "products/" + productId + "/" + variantId + "/" + fileName;
	}

	@Override
	public String getBannerImagePath(String fileName) {
		return "banners/" + fileName;
	}

	@Override
	public String getInvoicePath(String fileName) {
		return "invoices/" + fileName;
	}

	@Override
	public void deleteFile(String relativePath) {
		if (relativePath == null || relativePath.isEmpty()) return;
		if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
			log.info("Skipping deletion of externally hosted image: {}", relativePath);
			return;
		}
		try {
			Path filePath = Paths.get(uploadBasePath, relativePath);
			if (Files.exists(filePath)) {
				Files.delete(filePath);
				log.info("Deleted file: {}", relativePath);
			}
		} catch (IOException e) {
			log.error("Failed to delete file: {}", relativePath, e);
		} catch (java.nio.file.InvalidPathException e) {
			log.error("Invalid local file path, skipping deletion: {}", relativePath, e);
		}
	}

	@Override
	public void deleteImages(List<String> imagePaths) {
		if (imagePaths == null) return;
		for (String path : imagePaths) {
			deleteFile(path);
		}
	}

	@Override
	public byte[] getImage(String relativePath) {
		try {
			Path filePath = Paths.get(uploadBasePath, relativePath);
			if (!Files.exists(filePath)) {
				throw new RuntimeException("File not found: " + relativePath);
			}
			return Files.readAllBytes(filePath);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read file: " + relativePath, e);
		}
	}

	@Override
	public String getUploadBasePath() {
		return uploadBasePath;
	}
}
