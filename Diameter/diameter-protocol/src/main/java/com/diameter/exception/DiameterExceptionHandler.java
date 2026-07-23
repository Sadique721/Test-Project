package com.diameter.exception;

import com.diameter.exception.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ValidationException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class DiameterExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DiameterExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        logger.error("IllegalArgumentException at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException ex, HttpServletRequest request) {
        logger.error("SQLException at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Database error occurred.", request.getRequestURI());
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, HttpServletRequest request) {
        logger.warn("EmptyResultDataAccessException at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.NOT_FOUND, "No record found for the given criteria.", request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        logger.warn("Validation failed: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            errors.put(err.getField(), err.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleCustomValidationException(ValidationException ex, HttpServletRequest request) {
        logger.warn("Custom validation error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        logger.warn("ResponseStatusException at {}: {}", request.getRequestURI(), ex.getReason(), ex);

        ErrorResponse error = new ErrorResponse(
            ex.getRawStatusCode(),
            HttpStatus.valueOf(ex.getRawStatusCode()).getReasonPhrase(),
            ex.getReason(),
            request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.valueOf(ex.getRawStatusCode()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", request.getRequestURI());
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeParseException(DateTimeParseException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid date format. Expected format: yyyy-MM-dd HH:mm:ss", request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String paramName = ex.getName();
        Object value = ex.getValue();
        Class<?> requiredType = ex.getRequiredType();
        String expectedType = getReadableType(requiredType);
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected %s.", value, paramName, expectedType);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
        return new ResponseEntity<>(error, status);
    }

    private String getReadableType(Class<?> type) {

        if (type == null) {
            return "valid value";
        }

        if (type.equals(Integer.class) || type.equals(int.class) || type.equals(Long.class) || type.equals(long.class) || type.equals(BigInteger.class)) {
            return "numeric value";
        }

        if (type.equals(LocalDateTime.class)) {
            return "date-time in format yyyy-MM-dd HH:mm:ss";
        }

        if (type.equals(LocalDate.class)) {
            return "date in format yyyy-MM-dd";
        }

        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return "boolean (true/false)";
        }

        if (type.isEnum()) {
            return "one of " + Arrays.toString(type.getEnumConstants());
        }

        return type.getSimpleName();
    }
}