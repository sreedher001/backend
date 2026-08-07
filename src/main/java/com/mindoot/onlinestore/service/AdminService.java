package com.mindoot.onlinestore.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.model.ERole;
import com.mindoot.onlinestore.model.User;

@Component
public interface AdminService {

	List<User> getAllUsers();

	User getUserById(Long id);

	User updateUserRole(Long id, ERole role);

	User toggleUserStatus(Long id, boolean status);

	void deleteUser(Long id);

	
}
