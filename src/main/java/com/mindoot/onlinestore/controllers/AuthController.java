package com.mindoot.onlinestore.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mindoot.onlinestore.dto.MailDto;
import com.mindoot.onlinestore.dto.OtpRequest;
import com.mindoot.onlinestore.dto.OtpVerifyRequest;
import com.mindoot.onlinestore.dto.ResetPasswordRequest;
import com.mindoot.onlinestore.enums.PurchaseType;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.ERole;
import com.mindoot.onlinestore.model.Role;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.payload.request.LoginRequest;
import com.mindoot.onlinestore.payload.request.SignupRequest;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.payload.response.UserInfoResponse;
import com.mindoot.onlinestore.repository.RoleRepository;
import com.mindoot.onlinestore.repository.UserRepository;
import com.mindoot.onlinestore.security.jwt.JwtUtils;
import com.mindoot.onlinestore.security.services.UserDetailsImpl;
import com.mindoot.onlinestore.service.OtpAuthService;
import com.mindoot.onlinestore.service.serviceImpl.NotificationServiceImpl;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthController.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
	
	/** The authentication manager. */
	@Autowired
	AuthenticationManager authenticationManager;

	/** The user repository. */
	@Autowired
	UserRepository userRepository;

	/** The role repository. */
	@Autowired
	RoleRepository roleRepository;

	/** The encoder. */
	@Autowired
	PasswordEncoder encoder;

	/** The jwt utils. */
	@Autowired
	JwtUtils jwtUtils;

	/** The email service. */
	@Autowired
	NotificationServiceImpl emailService;
	
	@Autowired
	private OtpAuthService otpAuthService;

	@Value("${spring.mail.username}")
	private String from;

	@Value("${app.redirect.url}")
	private String appRedirectUrl;

	/**
	 * Authenticate user.
	 *
	 * @param loginRequest the login request
	 * @return the response entity
	 */
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		LOGGER.info("/signin api invoked..");
		LOGGER.info("Start - CLASS: AuthController, METHOD : authenticateUser ");
		// Fetch user by email
		User user = userRepository.findByEmail(loginRequest.getEmail())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

		// Check if email is verified
		if (!user.isEmailVerified()) {
			throw new ApplicationException("Email not verified. Please verify your email.", HttpStatus.FORBIDDEN);
		}
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		LOGGER.info("End - CLASS: AuthController, METHOD : authenticateUser ");
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
				.header("Access-Control-Expose-Headers", "Set-Cookie").body(new UserInfoResponse(userDetails.getId(),
						userDetails.getUsername(), userDetails.getEmail(), roles, jwtCookie.getValue().toString()));
	}

	/**
	 * Register user.
	 *
	 * @param signUpRequest the sign up request
	 * @return the response entity
	 * @throws MessagingException 
	 */
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws MessagingException {
//		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
//			return ResponseEntity.badRequest()
//					.body(new MessageResponse("Error: Username is already taken!", HttpStatus.BAD_REQUEST));
//		}
		
		
		LOGGER.info("/signup api invoked..");
		LOGGER.info("Start - CLASS: AuthController, METHOD : registerUser ");
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest()
					.body(new MessageResponse("Sorry: Email is already in use!", HttpStatus.BAD_REQUEST));
		}

		// Create new user's account (email + password only, phone/address added later)
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));
		user.setPreferredPurchaseType(PurchaseType.RETAIL);
		String token = UUID.randomUUID().toString();

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();
		if (strRoles == null || strRoles.isEmpty()) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new ApplicationException("Error: Role is not found.", HttpStatus.NOT_FOUND));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new ApplicationException("Error: Role is not found.", HttpStatus.NOT_FOUND));
					roles.add(adminRole);
					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new ApplicationException("Error: Role is not found.", HttpStatus.NOT_FOUND));
					roles.add(modRole);
					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new ApplicationException("Error: Role is not found.", HttpStatus.NOT_FOUND));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		user.setCreatedOn(LocalDate.now());
		user.setUpdatedOn(LocalDate.now());
		user.setVerificationToken(token);
		user.setEmailVerified(false);
		String otp = String.valueOf(new Random().nextInt(900000) + 100000);
		user.setOtp(otp);
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		User userdata = userRepository.save(user);
		LOGGER.info("New user created with unique id : {}", userdata.getId());
		
		
		

		
		MailDto mailDto = new MailDto();
		mailDto.setFrom(from);
		mailDto.setSendToAddress(user.getEmail());
		mailDto.setSubject("Verify your mail");
		mailDto.setTemplateName("welcome-email");
		mailDto.setBody(otp);
		mailDto.setUserName(user.getUsername());

		try {
			LOGGER.info("Invoking sendEmail method..");
			emailService.sendEmail(mailDto);
		} catch (Exception e) {
			LOGGER.warn("Failed to send verification email, but user was created: {}", e.getMessage());
		}

		LOGGER.info("End - CLASS: AuthController, METHOD : registerUser ");
		return ResponseEntity
				.ok(new MessageResponse("Account created successfully. Please verify your email when ready.", HttpStatus.OK));
	}

	/**
	 * Verify email.
	 *
	 * @param token the token
	 * @return the response entity
	 * @throws IOException 
	 */
	@GetMapping("/verify")
	public void verifyEmail(@RequestParam("token") String token,HttpServletResponse response) throws IOException {
		Optional<User> userOptional = userRepository.findByVerificationToken(token);

		if (userOptional.isEmpty()) {
			 response.sendRedirect(appRedirectUrl + "/auth/login?verified=failed");
		        return;
		}

		User user = userOptional.get();
		user.setEmailVerified(true);
		user.setVerificationToken(null); // Remove token after verification
		userRepository.save(user);
		
		response.sendRedirect(appRedirectUrl+"/auth/login?verified=success");

		
	}

	/**
	 * Forgot password.
	 *
	 * @param email the email
	 * @return the response entity
	 * @throws MessagingException the messaging exception
	 */
	@PostMapping("/forgot-password")
	public ResponseEntity<MessageResponse> forgotPassword(@RequestParam("email") String email)
			throws MessagingException {
		// Find user by email
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		// Generate 6-digit OTP
		String otp = String.valueOf(new Random().nextInt(900000) + 100000);

		// Set OTP expiry time (5 minutes from now)
		user.setOtp(otp);
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		userRepository.save(user);

		MailDto mailDto = new MailDto();
		mailDto.setFrom(from);
		mailDto.setSendToAddress(user.getEmail());
		mailDto.setSubject("OTP Verification");
		mailDto.setTemplateName("OTP-template");
		mailDto.setBody(otp);
		// Send OTP via email
		try {
			emailService.sendEmail(mailDto);
		} catch (Exception e) {
			LOGGER.warn("Failed to send OTP email to {}: {}", email, e.getMessage());
			throw new ApplicationException("Unable to send OTP email right now. Please try again later.",
					HttpStatus.SERVICE_UNAVAILABLE);
		}

		return ResponseEntity.ok(new MessageResponse("Enter the OTP sent to your registered email", HttpStatus.OK));
	}

	/**
	 * Verify otp.
	 *
	 * @param email the email
	 * @param otp the otp
	 * @return the response entity
	 */
	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@RequestParam("email") String email, @RequestParam("otp") String otp) {
		// Find user by email
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		// Check if OTP is valid and not expired
		if (user.getOtp() == null || !user.getOtp().equals(otp) || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new MessageResponse("Invalid or expired OTP.", HttpStatus.BAD_REQUEST));
		}
		
		user.setEmailVerified(true);
		// Do NOT clear the OTP here: this endpoint is also used as the OTP-check
		// step of the forgot-password flow (login.ts), which calls /reset-password
		// next with the same OTP. Clearing it here made every reset request fail
		// with "Invalid or expired OTP" regardless of the correct OTP being entered.
		// The OTP is still single-use and time-limited: /reset-password clears it
		// after a successful reset, and it naturally expires after 5 minutes either way.
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("OTP verified successfully.", HttpStatus.OK));
	}

	/**
	 * Reset password.
	 *
	 * @param request the reset request (email, otp, password) - sent as a JSON
	 *                body rather than query params, since query params get
	 *                logged in plaintext by servers/proxies/browser history.
	 * @return the response entity
	 */
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
		String email = request.getEmail();
		String otp = request.getOtp();
		String password = request.getPassword();

		// Find user by email
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		// Verify OTP before resetting password
		if (user.getOtp() == null || !user.getOtp().equals(otp) || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new MessageResponse("Invalid or expired OTP.", HttpStatus.BAD_REQUEST));
		}

		// Update password
		user.setPassword(encoder.encode(password));
		user.setOtp(null); // Clear OTP after successful reset
		user.setOtpExpiry(null);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("Password reset successful.", HttpStatus.OK));
	}

	/**
	 * Logout user.
	 *
	 * @return the response entity
	 */
	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser() {
		ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(new MessageResponse("You've been signed out!", HttpStatus.OK));
	}
	
	//OTP BASED AUTHENTICATION / AUTHORIZATION
	
	@PostMapping("/send-otp")
	public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request) {

	    otpAuthService.sendOtp(request.getValue());

	    return ResponseEntity.ok(
	        new MessageResponse("OTP sent successfully", HttpStatus.OK)
	    );
	}
	
	@PostMapping("/verify-otp-login")
	public ResponseEntity<?> verifyOtpLogin(@RequestBody OtpVerifyRequest request) {

	    User user = otpAuthService.verifyOtp(
	            request.getValue(),
	            request.getOtp()
	    );

	    UserDetailsImpl userDetails = UserDetailsImpl.build(user);

	    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

	    List<String> roles = userDetails.getAuthorities()
	            .stream().map(a -> a.getAuthority()).toList();

	    return ResponseEntity.ok()
	            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
	            .header("Access-Control-Expose-Headers", "Set-Cookie")
	            .body(new UserInfoResponse(
	                    user.getId(),
	                    user.getUsername(),
	                    user.getEmail(),
	                    roles,
	                    jwtCookie.getValue()
	            ));
	}
}
