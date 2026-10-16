package com.marketmesh.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO used to receive address details from API requests.
 * It validates required address fields and their allowed lengths.
 * The country field is optional, and defaultAddress indicates the default address status.
 */
@Data
public class AddressRequest {

    @NotBlank
    @Size(message = "Street must be between 2 and 100 characters", min = 2, max = 100)
    private String street;

    @NotBlank
    @Size(message = "City must be between 2 and 100 characters", min = 2, max = 100)
    private String city;

    @NotBlank
    @Size(message = "State must be between 2 and 100 characters", min = 2, max = 100)
    private String state;

    @NotBlank
    @Size(message = "Pincode must be between 6 and 6 characters", min = 6, max = 6)
    private String pincode;

    // Optional - defaults to "India" if left empty
    private String country;

    private boolean defaultAddress;
}