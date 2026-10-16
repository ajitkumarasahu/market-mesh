package com.marketmesh.address;

import com.marketmesh.address.dto.AddressRequest;
import com.marketmesh.address.dto.AddressResponse;
import com.marketmesh.user.User;
import com.marketmesh.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing user addresses.
 * It handles address creation, retrieval, updates, and deletion.
 * It also manages default addresses and ensures users access only their own addresses.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    /** Repository used to perform database operations on addresses. */
    private final AddressRepository addressRepository;

    /** Repository used to retrieve users by their email addresses. */
    private final UserRepository userRepository;

    /**
     * Creates a new address for the specified user.
     * The first address is automatically marked as the default address.
     * If another address is selected as default, the previous default is cleared.
     */
    public AddressResponse create(String email, AddressRequest request) {

        User user = findUser(email);
        List<Address> existing = addressRepository.findByUserId(user.getId());

        Address address = new Address();
        address.setUser(user);
        apply(address, request);

        // A user's first address is always the default
        boolean makeDefault = existing.isEmpty() || request.isDefaultAddress();
        if (makeDefault) {
            existing.forEach(a -> a.setDefault(false));
        }
        address.setDefault(makeDefault); // Lombok generates setDefault() for the "isDefault" field

        return toResponse(addressRepository.save(address));
    }

    /**
     * Retrieves all addresses belonging to the specified user.
     * Each address entity is converted into an AddressResponse DTO.
     * This method uses a read-only transaction for data retrieval.
     */
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses(String email) {
        User user = findUser(email);
        return addressRepository.findByUserId(user.getId())
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Updates an existing address owned by the specified user.
     * If the request marks the address as default, other addresses are unset as default.
     * Returns the updated address as an AddressResponse DTO.
     */
    public AddressResponse update(String email, Long id, AddressRequest request) {
        User user = findUser(email);
        Address address = findOwned(user, id);

        apply(address, request);

        if (request.isDefaultAddress()) {
            addressRepository.findByUserId(user.getId()).forEach(a -> a.setDefault(false));
            address.setDefault(true);
        }

        return toResponse(addressRepository.save(address));
    }

    /**
     * Deletes an address belonging to the specified user.
     * If the deleted address was the default, another remaining address is selected as default.
     * The deletion is flushed immediately to detect database constraint violations.
     */
    public void delete(String email, Long id) {
        User user = findUser(email);
        Address address = findOwned(user, id);
        boolean wasDefault = address.isDefault();

        addressRepository.delete(address);
        addressRepository.flush(); // fails here if an order already uses this address

        // Keep one default address if any remain
        if (wasDefault) {
            addressRepository.findByUserId(user.getId())
            .stream()
            .findFirst()
            .ifPresent(a -> a.setDefault(true));
        }
    }

    /**
     * Finds a user using their email address.
     * Returns the matching user when found.
     * Throws an IllegalArgumentException if the user does not exist.
     */
    private User findUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Returns the address only if it belongs to this user
    /**
     * Retrieves an address only when it belongs to the specified user.
     * If the address does not exist or belongs to another user, it throws an exception.
     * This prevents users from accessing or modifying other users' addresses.
     */
    private Address findOwned(User user, Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Address not found"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address not found");
        }
        return address;
    }

    /**
     * Copies address details from the request DTO to the address entity.
     * Updates the country only when a non-null, non-blank value is provided.
     * This method centralizes address field updates for creation and modification.
     */
    private void apply(Address address, AddressRequest request) {
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        if (request.getCountry() != null && !request.getCountry().isBlank()) {
            address.setCountry(request.getCountry());
        }
    }

    /**
     * Converts an Address entity into an AddressResponse DTO.
     * Copies the address location details and default address status.
     * Returns the DTO for use in API responses.
     */
    private AddressResponse toResponse(Address a) {

        return new AddressResponse(
            a.getStreet(),
            a.getCity(),
            a.getState(),
            a.getPincode(),
            a.getCountry(),
            a.isDefault()
        );
    }
}