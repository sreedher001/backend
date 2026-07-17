package com.mindoot.onlinestore.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.mindoot.onlinestore.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Value("${app.jwtCookieName}")
	private String jwtCookie;

	public String getJwtFromCookies(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, jwtCookie);
		if (cookie != null) {
			return cookie.getValue();
		} else {
			return null;
		}
	}

	public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
		String jwt = generateToken(userPrincipal);
		ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24 * 60 * 60).httpOnly(true)
				.build();
		return cookie;
	}

	public ResponseCookie getCleanJwtCookie() {
		ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
		return cookie;
	}

//	public String getUserNameFromJwtToken(String token) {
//		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
//	}
//	public String getEmailFromJwtToken(String token) {
//
//		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject(); // Subject
//																												// now
//																												// holds
//																												// the
//																												// email
//	}
	public String getEmailFromJwtToken(String token) {
		return getClaimsFromJwtToken(token).getSubject();
	}
	public Long getUserIdFromJwtToken(String token) {
	    Claims claims = getClaimsFromJwtToken(token);
	    return claims.get("id", Integer.class).longValue();
	}
	public String getPhoneFromJwtToken(String token) {
	    return getClaimsFromJwtToken(token).get("phone", String.class);
	}
	

	public Claims getClaimsFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
	}

	public List<String> getRolesFromJwtToken(String token) {
		List<String> roles = getClaimsFromJwtToken(token).get("roles", List.class);
		roles.forEach(role->{
			System.out.println(role);
		});
		return roles;
	}

	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public boolean validateJwtToken(String authToken) {
		try {
			// Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
			Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken).getBody();
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	public String generateToken(UserDetailsImpl userPrincipal) {
		return Jwts.builder().setSubject(userPrincipal.getEmail()).setIssuedAt(new Date())
				.claim("roles",
						userPrincipal.getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority())
								.toList())
				.claim("id", userPrincipal.getId())
				.claim("name", userPrincipal.getUsername())  // Full Name
		        .claim("email", userPrincipal.getEmail()) // Email
		        .claim("phone", userPrincipal.getPhoneNumber())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key(), SignatureAlgorithm.HS256).compact();
	}

}
