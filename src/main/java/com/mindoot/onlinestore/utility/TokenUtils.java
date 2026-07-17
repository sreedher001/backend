package com.mindoot.onlinestore.utility;

import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindoot.onlinestore.exception.ApplicationException;

// TODO: Auto-generated Javadoc
/**
 * The Class TokenUtils.
 */
@Component
public class TokenUtils {

	/**
	 * Gets the user info.
	 *
	 * @param token the token
	 * @return the user info
	 */
	public UserInfo getUserInfo(String token) {
		try {
			// Remove "Bearer " prefix if present
			if (token.startsWith("Bearer ")) {
				token = token.substring(7);
			}
			String[] parts = token.split("\\.");
			String payload = new String(Base64.getDecoder().decode(parts[1]));
			
			System.out.println("payload user info :"+payload);

			// Convert JSON string to UserInfo object
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(payload, UserInfo.class);
		} catch (Exception e) {
			throw new ApplicationException("Unable to get user info", HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Gets the user id.
	 *
	 * @param authorization the authorization
	 * @return the user id
	 */
	public Long getUserId(String authorization) {
		UserInfo userInfo = this.getUserInfo(authorization);
		Long userId = userInfo.getId();
		return userId;
	}
}
