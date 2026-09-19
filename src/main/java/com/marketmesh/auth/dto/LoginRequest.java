package com.marketmesh.auth.dto;

/**
 * LoginRequest.java
 * 
 * This class represents the data transfer object (DTO) for user login requests.
 * It contains fields for the user's email and password, along with validation
 * annotations to ensure that the input data meets the required criteria.
 *
 * The email field is validated to ensure it is a valid email format, not blank,
 * and has a length between 2 and 100 characters. The password field is validated
 * to ensure it is not blank and has a minimum length of 6 characters.
 *
 * This DTO is used in the AuthController to handle login requests and is passed
 * to the AuthService for authentication processing.
 */
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    /**
     * The email field represents the user's email address.
     * It is annotated with @Email to ensure that the input is a valid email format.
     * The @NotBlank annotation ensures that the field is not empty or null.
     * The @Size annotation restricts the length of the email to be between 2 and 100 characters.
     */
    @Email
    @NotBlank
    @Size(min = 2, max = 100, message = "Email must be between 2 and 100 characters")
    private String email;

    /**
     * The password field represents the user's password.
     * It is annotated with @NotBlank to ensure that the field is not empty or null.
     * The @Size annotation enforces a minimum length of 6 characters for the password.
     */
    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}         