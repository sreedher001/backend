package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.repository.UserRepository;
import com.mindoot.onlinestore.service.OtpService;

@Service
public class OtpServiceImpl implements OtpService{

	@Autowired
	 private  UserRepository userRepository;

	    public String generateOtp(User user) {
	        String otp = String.valueOf(100000 + new Random().nextInt(900000));
	        user.setOtp(otp);
	        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
	        userRepository.save(user);
	        return otp;
	    }

	    public boolean verifyOtp(User user, String otp) {
	        if (user.getOtp() == null || user.getOtpExpiry() == null) return false;
	        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) return false;
	        return user.getOtp().equals(otp);
	    }
}
