package com.marketmesh.auth.controller;

/**
 * AuthController.java
 * 
 * This class serves as the REST controller for authentication-related endpoints.
 * It handles user registration and login requests, delegating the business logic
 * to the AuthService. The controller ensures that incoming requests are validated
 * and returns appropriate HTTP responses with relevant data.
 *
 * Endpoints:
 * - POST /api/auth/register: Registers a new user with the provided details.
 * - POST /api/auth/login: Authenticates a user and returns a JWT token upon successful login.
 *
 * The controller uses ApiResponse to standardize the response format, providing
 * success messages and relevant data in the response body.
 *
 * Note: This class is annotated with @RestController, which combines @Controller
 * and @ResponseBody, allowing it to handle RESTful web service requests.
 */

import com.marketmesh.auth.dto.LoginRequest;
import com.marketmesh.auth.dto.LoginResponse;
import com.marketmesh.auth.dto.RegisterRequest;
import com.marketmesh.auth.dto.RegisterResponse;
import com.marketmesh.auth.service.AuthService;
import com.marketmesh.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController handles authentication-related HTTP requests.
 * It provides endpoints for user registration and login.
 */
@RestController

/**
 * The @RequestMapping annotation specifies the base URL for all endpoints in this controller.
 * All endpoints will be prefixed with "/api/auth".
 */
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * The AuthService is injected into the controller to handle the business logic
     * for user registration and login. It is marked as final to ensure immutability.
     */
    private final AuthService authService;

    /**
     * This post-method handles user registration. It accepts a RegisterRequest object, validates it,
     * and delegates the registration process to the AuthService. Upon successful registration,
     * it returns a ResponseEntity containing an ApiResponse with a success message and the registered user's details. The HTTP status code for a successful registration is 201 (Created).
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Registration successful", response));
    }

    /*This post-methosd handles user login. It accepts a LoginRequest object, validates it,
     *and delegates the login process to the AuthService. Upon successful login, 
     *it returns a ResponseEntity containing an ApiResponse with a success message and the logged-in user's details along with a JWT token. The HTTP status code for a successful login is 200 (OK).
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success("Login successful", response));
    }
}