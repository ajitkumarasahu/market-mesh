package com.marketmesh.address;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing Address entities.
 * This interface extends JpaRepository, providing CRUD operations and custom query methods for Address entities.
 * The findByUserId method allows retrieval of all addresses associated with a specific user by their user ID.
 * The @Repository annotation is not required as JpaRepository already includes it, but it can be added for clarity.
 * The AddressRepository interface is used to interact with the database for Address entities, enabling easy data
 */
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
}