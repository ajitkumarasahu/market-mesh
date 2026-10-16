package com.marketmesh.address.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used to return address details in API responses.
 * It contains the address location information and default address status.
 * Lombok generates getters, setters, and constructors automatically.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private String street;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private boolean defaultAddress;
}