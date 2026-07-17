package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.enums.PurchaseType;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.ERole;
import com.mindoot.onlinestore.model.Role;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.repository.RoleRepository;
import com.mindoot.onlinestore.repository.UserRepository;
import com.mindoot.onlinestore.security.services.UserDetailsImpl;
import com.mindoot.onlinestore.service.OtpAuthService;
import com.mindoot.onlinestore.service.SnsSmsService;

@Service
public class OtpAuthServiceImpl implements OtpAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationServiceImpl notificationService;
    
    @Autowired 
    private SnsSmsService snsSmsService;
    
    @Autowired
    private RoleRepository roleRepository;
    

    @Autowired
    private PasswordEncoder encoder;
    

    private String normalizePhone(String phone) {
        if (!phone.startsWith("+91")) {
            phone = "+91" + phone.replaceFirst("^0+", "");
        }
        return phone;
    }

    @Override
    public void sendOtp(String phoneNumber) {

        phoneNumber = normalizePhone(phoneNumber);

        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        User user;

        if (userOpt.isPresent()) {
            //  Existing user
            user = userOpt.get();

        } else {
            //  Check if logged-in user (email user linking phone)
            org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated()
                    && auth.getPrincipal() instanceof UserDetailsImpl userDetails) {

                user = userRepository.findById(userDetails.getId()).orElseThrow();

                //  Link phone
                user.setPhoneNumber(phoneNumber);

            } else {
                //  Guest user ->	 create new
                user = new User();
                user.setPhoneNumber(phoneNumber);
                user.setUsername("user_" + phoneNumber.substring(phoneNumber.length() - 4));
                user.setPassword(encoder.encode(UUID.randomUUID().toString()));
                user.setPhoneNumberVerified(false);
                user.setPreferredPurchaseType(PurchaseType.RETAIL);
                
             // Assign ROLE_USER
                Role role = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new ApplicationException("Error: Role not found",HttpStatus.BAD_REQUEST));

                user.setRoles(Set.of(role));
            }
        }

        //  Generate OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        
        snsSmsService.sendOtpSms(phoneNumber, otp);
    }

    @Override
    public User verifyOtp(String phoneNumber, String otp) {

        phoneNumber = normalizePhone(phoneNumber);

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ApplicationException("User not found",HttpStatus.BAD_REQUEST));

        if (user.getOtp() == null ||
            !user.getOtp().equals(otp) ||
            user.getOtpExpiry().isBefore(LocalDateTime.now())) {

            throw new ApplicationException("Invalid or expired OTP",HttpStatus.BAD_REQUEST);
        }

        //  Mark verified
        user.setPhoneNumberVerified(true);

        // Clear OTP
        user.setOtp(null);
        user.setOtpExpiry(null);

        return userRepository.save(user);
    }
}
