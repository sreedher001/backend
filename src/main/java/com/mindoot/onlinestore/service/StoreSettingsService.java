package com.mindoot.onlinestore.service;

import com.mindoot.onlinestore.model.StoreSettings;

public interface StoreSettingsService {

    StoreSettings getSettings();

    StoreSettings updateSettings(StoreSettings updated);
}
