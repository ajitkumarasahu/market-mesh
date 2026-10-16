package com.marketmesh.address;

import com.marketmesh.address.dto.AddressRequest;
import com.marketmesh.address.dto.AddressResponse;
import com.marketmesh.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user address-related API requests.
 * It provides endpoints for creating, retrieving, updating, and deleting addresses.
 * Authenticated user details are used to identify the owner of each address.
 */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    /**
     * Service used to perform address-related business operations.
     * It handles address creation, retrieval, updating, and deletion
     * for the authenticated user.
     */
    private final AddressService addressService;

    /**
     * Creates a new address for the authenticated user.
     * Validates the request data and passes the user's identity to the service.
     * Returns the created address with HTTP 201 CREATED status.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(Authentication authentication,@Valid @RequestBody AddressRequest request) {
                                                               
        AddressResponse response = addressService.create(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Address added successful", response));
    }

    /**
     * Retrieves all addresses belonging to the authenticated user.
     * Uses the authenticated user's identity to fetch the associated addresses.
     * Returns the address list with HTTP 200 OK status.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMine(Authentication authentication) {

        List<AddressResponse> response = addressService.getMyAddresses(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Addresses fetched successful", response));
    }

    /**
     * Updates an existing address belonging to the authenticated user.
     * Uses the address ID and validated request data to perform the update.
     * Returns the updated address with HTTP 200 OK status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> update(Authentication authentication,@PathVariable Long id,@Valid @RequestBody AddressRequest request) {
                                                                                                                               
        AddressResponse response = addressService.update(authentication.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated successful", response));
    }

    /**
     * Deletes an address belonging to the authenticated user.
     * Passes the authenticated user's identity and address ID to the service.
     * Returns a success response with HTTP 200 OK status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(Authentication authentication, @PathVariable Long id) {

        addressService.delete(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successful", null));
    }
}