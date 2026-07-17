package com.mindoot.onlinestore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.OtpRequest;
import com.mindoot.onlinestore.dto.OtpVerifyRequest;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.service.UserService;
import com.mindoot.onlinestore.utility.TokenUtils;
import com.mindoot.onlinestore.utility.UserInfo;

/**
 * The Class UserController.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

	/** The user service. */
	@Autowired
	private UserService userService;

	@Autowired
	private TokenUtils tokenUtils;
	/**
	 * Gets the user profile.
	 *
	 * @param email the email
	 * @return the user profile
	 */
	@GetMapping("/profile")
	public ResponseEntity<User> getUserProfile(@RequestParam("email") String email) {
		User userProfile = userService.getUserProfile(email);
		return ResponseEntity.ok(userProfile);
	}

	/**
	 * Update user profile.
	 *
	 * @param email the email
	 * @param updatedUser the updated user
	 * @return the response entity
	 */
	@PostMapping("/profile")
	public ResponseEntity<User> updateUserProfile(@RequestParam("email") String email, @RequestBody User updatedUser) {
		return ResponseEntity.ok(userService.updateUserProfile(email, updatedUser));
	}
	
	@PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request, @RequestHeader("Authorization") String authorization) {
		
		UserInfo userInfo = this.tokenUtils.getUserInfo(authorization);
		Long userId = userInfo.getId();
		userService.sendOtp(userId, request);
        return ResponseEntity.ok(new MessageResponse("OTP sent successfully",HttpStatus.OK));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest request, @RequestHeader("Authorization") String authorization) {
    	UserInfo userInfo = this.tokenUtils.getUserInfo(authorization);
		Long userId = userInfo.getId();
    	userService.verifyOtp(userId, request);
        return ResponseEntity.ok(new MessageResponse("Verified and updated successfully",HttpStatus.OK));
    }
}
