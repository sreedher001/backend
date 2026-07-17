package com.mindoot.onlinestore.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, HttpStatus httpStatus) {
        super(message);
    }
}
