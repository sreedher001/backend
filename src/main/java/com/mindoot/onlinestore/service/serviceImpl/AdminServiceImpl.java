package com.mindoot.onlinestore.service.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.Role;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.repository.UserRepository;
import com.mindoot.onlinestore.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	// Fetch a single user by ID
	@Override
	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ApplicationException("User not found",HttpStatus.NOT_FOUND));
	}

	@Override
	public User updateUserRole(Long id, Role role) {
		User user = userRepository.findById(id).orElseThrow(() -> new ApplicationException("User not found",HttpStatus.NOT_FOUND));
		Set<Role> roles = new HashSet<>();
		roles.addAll(user.getRoles());
		roles.add(role);
		user.setRoles(roles);
		return userRepository.save(user);
	}

	// Enable or disable a user
	@Override
	public User toggleUserStatus(Long id, boolean status) {
		User user = userRepository.findById(id).orElseThrow(() -> new ApplicationException("User not found",HttpStatus.NOT_FOUND));
		user.setEnabled(status);
		return userRepository.save(user);
	}

	@Override
	// Delete user
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}
}
