package com.mindoot.onlinestore.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.model.Role;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.service.AdminService;

import lombok.RequiredArgsConstructor;

// TODO: Auto-generated Javadoc
/**
 * The Class AdminController.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

	
	/** The admin service. */
	@Autowired
    private AdminService adminService;

    /**
     * Gets the all users.
     *
     * @return the all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Gets the user by id.
     *
     * @param id the id
     * @return the user by id
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    /**
     * Update user role.
     *
     * @param id the id
     * @param role the role
     * @return the response entity
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable("id") Long id, @RequestParam("role") Role role) {
        return ResponseEntity.ok(adminService.updateUserRole(id, role));
    }

    /**
     * Toggle user status.
     *
     * @param id the id
     * @param status the status
     * @return the response entity
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<User> toggleUserStatus(@PathVariable("id") Long id, @RequestParam boolean status) {
        return ResponseEntity.ok(adminService.toggleUserStatus(id, status));
    }

    /**
     * Delete user.
     *
     * @param id the id
     * @return the response entity
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    
}
