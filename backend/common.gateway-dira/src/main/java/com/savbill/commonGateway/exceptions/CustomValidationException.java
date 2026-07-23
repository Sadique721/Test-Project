package com.savbill.commonGateway.exceptions;

import org.springframework.http.HttpStatus;

public class CustomValidationException extends RuntimeException {
    private int statusCode;
    private String message;

    public CustomValidationException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}