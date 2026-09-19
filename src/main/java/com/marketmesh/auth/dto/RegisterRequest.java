package com.marketmesh.auth.dto;

/**
 * RegisterRequest.java
 * 
 * This class represents the data transfer object (DTO) for user registration requests.
 * It contains fields for the user's name, email, password, and phone number, along with
 * validation annotations to ensure that the input data meets the required criteria.
 *
 * The name field is validated to ensure it is not blank and has a length between 2 and 100 characters.
 * The email field is validated to ensure it is a valid email format, not blank, and has a length between 2 and 100 characters.
 * The password field is validated to ensure it is not blank and has a minimum length of 6 characters.
 * The phone field is validated to ensure it is not blank and has a length between 10 and 15 characters.
 *
 * This DTO is used in the AuthController to handle registration requests and is passed
 * to the AuthService for processing.
 */
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    /**
     * The name field represents the user's full name.
     * It is annotated with @NotBlank to ensure that the field is not empty or null.
     * The @Size annotation restricts the length of the name to be between 2 and 100 characters.
     */
    @NotBlank
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

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
     * The @Size annotation enforces a m    inimum length of 6 characters for the password.
     */
    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /**
     * The phone field represents the user's phone number.
     * It is annotated with @NotBlank to ensure that the field is not empty or null.
     * The @Size annotation restricts the length of the phone number to be between 10 and 15 characters.
     */
    @NotBlank
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phone;
}