package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.OtpRequest;
import com.mindoot.onlinestore.dto.OtpVerifyRequest;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.repository.UserRepository;
import com.mindoot.onlinestore.service.EmailService;
import com.mindoot.onlinestore.service.OtpService;
import com.mindoot.onlinestore.service.SnsSmsService;
import com.mindoot.onlinestore.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OtpService otpService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired 
	private SnsSmsService snsSmsService;

	@Override
	public User getUserProfile(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	@Override
	public User updateUserProfile(String email, User updatedUser) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ApplicationException("User not found",HttpStatus.BAD_REQUEST));

		user.setUsername(updatedUser.getUsername());
		user.setPhoneNumber(updatedUser.getPhoneNumber());
		user.setAddress(updatedUser.getAddress());
		user.setUpdatedOn(LocalDate.now());
		return userRepository.save(user);
	}

	@Transactional
    public void sendOtp(Long userId, OtpRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException("User not found",HttpStatus.BAD_REQUEST));

        String otp = otpService.generateOtp(user);

        if ("email".equalsIgnoreCase(request.getField())) {
            emailService.sendOtpEmail(request.getValue(), otp,user);
        } else if ("phone".equalsIgnoreCase(request.getField())) {
        	snsSmsService.sendOtpSms(request.getValue(), otp);
        } else {
            throw new ApplicationException("Invalid field type",HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public void verifyOtp(Long userId, OtpVerifyRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException("User not found",HttpStatus.BAD_REQUEST));

        boolean valid = otpService.verifyOtp(user, request.getOtp());
        if (!valid) throw new RuntimeException("Invalid or expired OTP");

        if ("email".equalsIgnoreCase(request.getField())) {
            user.setEmail(request.getValue());
            user.setEmailVerified(true);
        } else if ("phone".equalsIgnoreCase(request.getField())) {
            user.setPhoneNumber(request.getValue());
        }

        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }

}
