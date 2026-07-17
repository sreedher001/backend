package com.mindoot.onlinestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

	List<Banner> findAllByOrderByUploadedAtDesc();
}
