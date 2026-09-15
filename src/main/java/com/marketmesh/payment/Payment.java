package com.marketmesh.payment;

/*
 * Represents a payment transaction.
 * import com.marketmesh.order.Order it use for
 */
import com.marketmesh.order.Order;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/* table name is "payments" and it has the following columns:
 * id: primary key, auto-generated
 * order_id: foreign key to the orders table, unique, not null
 * payment_method: string, not null
 * payment_status: enum, not null, default value is PENDING
 * transaction_id: string, unique, not null
 * amount: decimal, not null
 * paid_at: datetime, not null
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // It represents the order associated with the payment.
    // It establishes a one-to-one relationship with the Order entity.
    // It connects the order_id column in the payments table to the id column in the orders table.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private String transactionId;

    private BigDecimal amount;

    private LocalDateTime paidAt;
}