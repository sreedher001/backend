package com.mindoot.onlinestore.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepositoryRepository extends JpaRepository<Category, Long> {

}
