package com.mindoot.onlinestore.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mindoot.onlinestore.model.StoreSettings;
import com.mindoot.onlinestore.service.LocalFileStorageService;
import com.mindoot.onlinestore.service.StoreSettingsService;

@RestController
public class StoreSettingsController {

    @Autowired
    private StoreSettingsService storeSettingsService;

    @Autowired
    private LocalFileStorageService fileStorageService;

    /**
     * Public: the storefront needs this on every page load (topbar, footer,
     * SEO tags, checkout, terms & conditions) so it must not require auth.
     */
    @GetMapping("/api/store-settings")
    public ResponseEntity<StoreSettings> getSettings() {
        return ResponseEntity.ok(storeSettingsService.getSettings());
    }

    @PutMapping("/api/admin/store-settings")
    public ResponseEntity<StoreSettings> updateSettings(@RequestBody StoreSettings settings) {
        return ResponseEntity.ok(storeSettingsService.updateSettings(settings));
    }

    @PostMapping(value = "/api/admin/store-settings/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadImage(@RequestPart("file") MultipartFile file) throws IOException {
        String url = fileStorageService.uploadImage(file, "store-settings");
        return ResponseEntity.ok(Map.of("url", url));
    }
}
