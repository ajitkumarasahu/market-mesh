package com.marketmesh.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Finds a cart by the associated user's ID.
     *
     * @param userId the ID of the user
     * @return an Optional containing the Cart if found, or empty if not found
     */
    Optional<Cart> findByUserId(Long userId);
}