package com.mindoot.onlinestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.Reel;

@Repository
public interface ReelRepository extends JpaRepository<Reel, Long> {
	List<Reel> findByIsActiveTrue();
}
