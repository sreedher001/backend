package com.mindoot.onlinestore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.ERole;
import com.mindoot.onlinestore.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);
	
	Optional<User> findByVerificationToken(String token);

	@Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :role")
	List<User> findAllByRole(@Param("role") ERole role);

	boolean existsByPhoneNumber(String phoneNumber);
	
	Optional<User> findByPhoneNumber(String phoneNumber);
	
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
	Optional<User> findByIdWithRoles(@Param("id") Long id);

	
}
