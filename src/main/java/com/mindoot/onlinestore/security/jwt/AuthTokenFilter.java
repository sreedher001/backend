package com.mindoot.onlinestore.security.jwt;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindoot.onlinestore.security.services.UserDetailsServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
@Component

public class AuthTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	 // Add CORS headers constant
    private static final String ALLOWED_ORIGIN = "https://d12okeuel29ts0.cloudfront.net";
    private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD";
    private static final String ALLOWED_HEADERS = "Content-Type, content-type, Authorization, authorization, X-Amz-Date, X-Api-Key, X-Amz-Security-Token, Accept, Origin, X-Requested-With, x-requested-with";
    static final String ALLOW_CREDENTIALS = "true";
    private static final String MAX_AGE = "3600"; // 1 hour in seconds
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		
		// Normalize API Gateway stage from the URI path
		String originalPath = request.getRequestURI();
		String normalizedPath = normalizePath(originalPath);
		logger.info("Original path: {}, Normalized path: {}", originalPath, normalizedPath);

		// Wrap the request to override all path methods
		HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
			@Override
			public String getRequestURI() {
				return normalizedPath;
			}

			@Override
			public String getServletPath() {
				return normalizedPath;
			}

			@Override
			public String getPathInfo() {
				return normalizedPath;
			}
		};
		
		
	    String method = request.getMethod();

	    // Define exact public endpoints by path and method
	    boolean isPublicEndpoint =
	            (normalizedPath.equals("/api/products/all-products") || 
	            		normalizedPath.startsWith("/api/products/variants/")||
	            		normalizedPath.startsWith("/api/auth/signin")||normalizedPath.startsWith("/api/auth/signup")||
	            		normalizedPath.startsWith("/api/auth/verify")||
	            		normalizedPath.startsWith("/api/products/search")||
	            		normalizedPath.startsWith("/api/products/autocomplete")||                                 
	            		normalizedPath.startsWith("/sns/sendsms")||
	            		normalizedPath.startsWith("/api/webhook/razorpay")||
	            		normalizedPath.startsWith("/api/webhook/order-tracking")||
	            		normalizedPath.startsWith("/api/ai/suggestions")||
	            		normalizedPath.startsWith("/api/banners/all-banners")||
	            		normalizedPath.startsWith("/actuator")||
	            		normalizedPath.startsWith("/api/auth/forgot-password")||
	            		normalizedPath.startsWith("/api/auth/reset-password")||
	            		normalizedPath.startsWith("/api/interest")||
	            		normalizedPath.startsWith("/api/reels/")||
	            		normalizedPath.startsWith("/api/cart/")||

	            		normalizedPath.startsWith("/api/auth/send-otp")||
	            		normalizedPath.startsWith("/api/auth/verify-otp-login")||

	            		normalizedPath.startsWith("/api/products/category/search")||
	                    normalizedPath.matches("^/api/reviews/\\d+/reviews$")
	            		);
	    
	    
	    
	    if (!isPublicEndpoint) {
	        logger.info("Secured endpoint, auth required: " + normalizedPath);
	    }
	    if (!isPublicEndpoint) {
	       
		try {

			String requestURI = wrappedRequest.getRequestURI();

//			if (requestURI.startsWith("/api/auth/")||requestURI.startsWith("/api/payments/")) {
//				filterChain.doFilter(wrappedRequest, response);
//				return;
//			}
			String jwt = parseJwt(wrappedRequest);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				// String username = jwtUtils.getUserNameFromJwtToken(jwt);
				String email = jwtUtils.getEmailFromJwtToken(jwt);
				Long userId = jwtUtils.getUserIdFromJwtToken(jwt);
				UserDetails userDetails = userDetailsService.loadUserById(userId);
//				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				List<String> roles = jwtUtils.getRolesFromJwtToken(jwt);
				List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, authorities);

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(wrappedRequest));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}catch (ExpiredJwtException ex) {
		    logger.warn("JWT Token expired: {}", ex.getMessage());

		    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    response.setContentType("application/json");

		    var responseBody = new ObjectMapper().writeValueAsString(
		        java.util.Map.of("message", "Token has expired")
		    );
		    response.getWriter().write(responseBody);
		    return;
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e.getMessage(), e);
		}
	    }

		filterChain.doFilter(wrappedRequest, response);
	}

	private String parseJwt(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}

	private String normalizePath(String path) {
		final String[] STAGES = { "/dev/", "/prod/", "/qa/", "/stage/" };
		for (String stage : STAGES) {
			if (path.startsWith(stage)) {
				return path.substring(stage.length() - 1);
				//return "/" + path.substring(stage.length());
			}
		}
		return path;
	}
	
	


}
