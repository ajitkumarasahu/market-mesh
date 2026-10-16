package com.marketmesh.common.exception;

import org.springframework.dao.DataIntegrityViolationException;
import com.marketmesh.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * It catches and handles common application, validation, and security exceptions.
 * It provides consistent API error responses and prevents internal error details
 * from being exposed directly to the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles IllegalArgumentException thrown by service-layer business logic.
     * Returns a BAD_REQUEST response with the exception message.
     * This can be used for errors such as missing categories or duplicate data.
     */
    // Thrown manually throughout your services for business logic errors
    // e.g. "Category not found", "Email already registered", "Invalid email or password"
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail(ex.getMessage(), null));
    }

    /**
     * Handles validation errors triggered by the @Valid annotation.
     * Collects validation errors for each field and returns them in a map.
     * Returns a BAD_REQUEST response containing the field-level error messages.
     */
    // Triggered automatically by @Valid when a DTO fails validation
    // (e.g. @NotBlank, @Email, @Min violated) - returns field-level error messages
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail("Validation failed", fieldErrors));
    }

    /**
     * Handles authorization failures when an authenticated user lacks permission.
     * This occurs when a user's role does not have access to a protected endpoint.
     * Returns a FORBIDDEN response with an appropriate permission error message.
     */
    // Spring Security throws this when a valid token belongs to a role
    // without permission for the route (e.g. USER trying ADMIN-only endpoint)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.fail("You don't have permission to perform this action", null));
    }

    /**
     * Handles authentication failures caused by invalid login credentials.
     * This exception is commonly thrown when the provided username or password
     * does not match the expected authentication details.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.fail("Invalid credentials", null));
    }

    /**
     * Handles unexpected exceptions that are not handled by specific handlers.
     * Returns a generic INTERNAL_SERVER_ERROR response without exposing
     * internal stack traces or sensitive application details to the client.
     */
    // Catch-all safety net - anything unexpected (NullPointerException, DB errors, etc.)
    // Prevents leaking raw stack traces to the client
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail("Something went wrong. Please try again later.", null));
    }


    // "Product not found", "Your cart is empty", "Not enough stock", "Invalid email or password", etc.
    // @ExceptionHandler(IllegalArgumentException.class)
    // public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
    //     return ResponseEntity
    //        .status(HttpStatus.BAD_REQUEST)
    //        .body(ApiResponse.fail(ex.getMessage(), null));
    // }

    // Deleting a record that other tables still reference (e.g. a product inside an order),
    // or inserting a duplicate unique value
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(DataIntegrityViolationException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.fail(ex.getMessage(), null));
    }
}