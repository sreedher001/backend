package com.mindoot.onlinestore.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//ApplicationException.java
@Getter
@Setter
@NoArgsConstructor
@Builder
public class ApplicationException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private HttpStatus httpStatus;

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(String message, HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
