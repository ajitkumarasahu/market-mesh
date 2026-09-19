package com.marketmesh.auth.service;

/**
 * AuthService.java
 * 
 * This class serves as the service layer for authentication-related operations.
 * It handles the business logic for user registration and login, interacting with
 * the UserRepository to persist user data and using PasswordEncoder for secure
 * password handling. The service also utilizes JwtUtil to generate JWT tokens for
 * authenticated users.
 *
 * Methods:
 * - register(RegisterRequest request): Registers a new user by validating the input,
 *   checking for existing email, hashing the password, and saving the user to the database.
 * - login(LoginRequest request): Authenticates a user by verifying the email and password,
 *   and generates a JWT token upon successful authentication.
 *
 * Note: This class is annotated with @Service, indicating that it is a Spring service component.
 */
import com.marketmesh.auth.dto.LoginRequest;
import com.marketmesh.auth.dto.LoginResponse;
import com.marketmesh.auth.dto.RegisterRequest;
import com.marketmesh.auth.dto.RegisterResponse;
import com.marketmesh.config.JwtUtil;
import com.marketmesh.user.Role;
import com.marketmesh.user.User;
import com.marketmesh.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * The UserRepository is injected into the service to handle database operations
     * related to user entities. It is marked as final to ensure immutability.
     * 1.call userRepository.existsByEmail(request.getEmail()) to check if the email is already registered.
     * 2.call userRepository.save(user) to save the new user to the database.
     * 3.call userRepository.findByEmail(request.getEmail()) to retrieve the user by email during login.
     */
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * This method handles user registration. It accepts a RegisterRequest object,
     * validates the input, checks for existing email, hashes the password, and saves
     * the new user to the database. Upon successful registration, it returns a
     * RegisterResponse containing the registered user's details.
     *
     * @param request The RegisterRequest object containing user registration details.
     * @return A RegisterResponse object with the registered user's information.
     * @throws IllegalArgumentException if the email is already registered.
     */
    public RegisterResponse register(RegisterRequest request) {

        //if email is already registered throw an exception
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Create a new User (user object) entity and set its properties based on the registration request
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // hash before saving - never store plain text
        user.setPhone(request.getPhone());
        user.setRole(Role.USER);

        User saved = userRepository.save(user);

        //return a RegisterResponse with the registered user's details
        return new RegisterResponse(
            saved.getName(),
            saved.getEmail(),
            saved.getRole().name()
        );
    }

    /**
     * This method handles user login. It accepts a LoginRequest object, verifies the
     * user's email and password, and generates a JWT token upon successful authentication.
     * If the email or password is invalid, it throws an IllegalArgumentException.
     *
     * @param request The LoginRequest object containing user login details.
     * @return A LoginResponse object with the authenticated user's information and JWT token.
     * @throws IllegalArgumentException if the email or password is invalid.
     */
    public LoginResponse login(LoginRequest request) {

        // Retrieve the user by email from the database, or throw an exception if not found
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
            
        /* Verify the provided password against the stored hashed password using PasswordEncoder.
         *If the passwords do not match, throw an exception.
        */
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate a JWT token for the authenticated user
        String token = jwtUtil.generateToken(user.getEmail());

        // Return a LoginResponse with the user's details and the generated token
        return new LoginResponse(
            user.getName(),
            user.getEmail(),
            user.getRole().name(),
            token
        );
    }
}