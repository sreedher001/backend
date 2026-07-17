package com.mindoot.onlinestore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.ReelLike;

@Repository
public interface ReelLikeRepository extends JpaRepository<ReelLike, Long> {

    Optional<ReelLike> findByReelIdAndUserId(Long reelId, Long userId);

    Optional<ReelLike> findByReelIdAndDeviceId(Long reelId, String deviceId);

	boolean existsByReelIdAndUserId(Long id, Long userId);

	boolean existsByReelIdAndDeviceId(Long id, String deviceId);

}