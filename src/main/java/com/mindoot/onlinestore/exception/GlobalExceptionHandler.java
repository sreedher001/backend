package com.mindoot.onlinestore.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
//GlobalExceptionHandler.java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;

import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ConfigDataResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ConfigDataResourceNotFoundException ex, WebRequest request) {
		ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(),
				request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, WebRequest request) {
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(),
				request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex, WebRequest request) {
	    ErrorResponse error = new ErrorResponse(
	        ex.getHttpStatus().value(),
	        ex.getMessage(),
	        ex.getMessage(),
	        request.getDescription(false).replace("uri=", "")
	    );
	    return new ResponseEntity<>(error, ex.getHttpStatus());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
				.collect(Collectors.joining(", "));
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation Failed", message,
				request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ErrorResponse> handleFileUploadingError(Exception ex, WebRequest request) {
		logger.error("Error Occured !: {}", ex.getMessage());
		
		logger.error("Error Occured !: {}", ex);
		System.out.println("Error Occured !: {}"+ ex);
		ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"MultipartException- custom exception Internal Server Error", ex.getMessage(),
				request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
		logger.error("Error Occured !: {}", ex.getMessage());
		
		logger.error("Error Occured !: {}", ex);
		ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"custom exception Internal Server Error", ex.getMessage(),
				request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
