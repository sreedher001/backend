package com.mindoot.onlinestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.StoreSettings;

@Repository
public interface StoreSettingsRepository extends JpaRepository<StoreSettings, Long> {
}
