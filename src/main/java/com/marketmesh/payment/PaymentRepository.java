package com.marketmesh.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Finds a payment by the associated order ID.
    // It returns an Optional containing the Payment if found, or an empty Optional if not found.
    Optional<Payment> findByOrderId(Long orderId);
}