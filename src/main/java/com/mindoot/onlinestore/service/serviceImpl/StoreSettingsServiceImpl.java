package com.mindoot.onlinestore.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.model.StoreSettings;
import com.mindoot.onlinestore.repository.StoreSettingsRepository;
import com.mindoot.onlinestore.service.StoreSettingsService;

import jakarta.transaction.Transactional;

@Service
public class StoreSettingsServiceImpl implements StoreSettingsService {

    @Autowired
    private StoreSettingsRepository storeSettingsRepository;

    @Override
    @Transactional
    public StoreSettings getSettings() {
        StoreSettings settings = storeSettingsRepository.findAll().stream().findFirst().orElseGet(this::createDefaults);

        // Backfills columns added after row #1 already existed on this deployment,
        // so upgrading this feature never leaves the storefront without a brand color.
        if (settings.getPrimaryColor() == null || settings.getPrimaryColor().isBlank()) {
            settings.setPrimaryColor("#c2410c");
            settings = storeSettingsRepository.save(settings);
        }

        return settings;
    }

    @Override
    @Transactional
    public StoreSettings updateSettings(StoreSettings updated) {
        StoreSettings settings = getSettings();

        settings.setStoreName(updated.getStoreName());
        settings.setTagline(updated.getTagline());
        settings.setAboutDescription(updated.getAboutDescription());
        settings.setLogoUrl(updated.getLogoUrl());
        settings.setFaviconUrl(updated.getFaviconUrl());
        settings.setSupportEmail(updated.getSupportEmail());
        settings.setSupportPhone(updated.getSupportPhone());
        settings.setAddressLine(updated.getAddressLine());
        settings.setCity(updated.getCity());
        settings.setState(updated.getState());
        settings.setCountry(updated.getCountry());
        settings.setPostalCode(updated.getPostalCode());
        settings.setInstagramUrl(updated.getInstagramUrl());
        settings.setFacebookUrl(updated.getFacebookUrl());
        settings.setTwitterUrl(updated.getTwitterUrl());
        settings.setLinkedinUrl(updated.getLinkedinUrl());
        settings.setWebsiteDomain(updated.getWebsiteDomain());
        settings.setSeoTitle(updated.getSeoTitle());
        settings.setSeoDescription(updated.getSeoDescription());
        settings.setCurrencySymbol(updated.getCurrencySymbol());
        settings.setPrimaryColor(updated.getPrimaryColor());

        return storeSettingsRepository.save(settings);
    }

    /**
     * Seeds row #1 with the values already live on this deployment, so
     * enabling this feature doesn't change anything a visitor sees until
     * an admin actually edits Store Settings.
     */
    private StoreSettings createDefaults() {
        StoreSettings settings = new StoreSettings();
        settings.setStoreName("Bueno Exports");
        settings.setTagline("Authentic spices, sourced with care.");
        settings.setAboutDescription("Bueno Exports brings you the finest selection of authentic spices, tea powders, masalas, coffee powders, dry fruits, and herbs sourced directly from trusted growers across India.");
        settings.setLogoUrl("assets/images/logo.png");
        settings.setFaviconUrl("assets/images/logo.png");
        settings.setSupportEmail("info@buenoexports.com");
        settings.setSupportPhone("8270741734");
        settings.setAddressLine("Devala");
        settings.setCity("Gudalur, Nilgiris");
        settings.setState("Tamil Nadu");
        settings.setCountry("India");
        settings.setPostalCode("643270");
        settings.setWebsiteDomain("https://buenoexports.com");
        settings.setSeoTitle("Bueno Exports | Authentic Indian Spices, Retail & Wholesale");
        settings.setSeoDescription("Bueno Exports brings you authentic spices, tea powders, masalas, coffee powders, dry fruits, and herbs sourced directly from trusted growers across India. Shop retail packs or wholesale quantities.");
        settings.setCurrencySymbol("₹");
        settings.setPrimaryColor("#c2410c");
        return storeSettingsRepository.save(settings);
    }
}
