package com.mindoot.onlinestore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.model.WishlistItem;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

	List<WishlistItem> findByUser(User user);

    Optional<WishlistItem> findByUserAndProductVariant(User user, ProductVariant variant);

    void deleteByUserAndProductVariant(User user, ProductVariant variant);

    boolean existsByUserAndProductVariant(User user, ProductVariant variant);
}
