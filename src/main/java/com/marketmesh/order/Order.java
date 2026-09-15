package com.marketmesh.order;

/*
* import com.marketmesh.address.Address;
* import com.marketmesh.user.User;
* import jakarta.persistence.*;
* import lombok.Data;
* import lombok.NoArgsConstructor;
 */
import com.marketmesh.address.Address;
import com.marketmesh.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
* Represents an order placed by a user. 
* it contain fields id, user_id, adress_id, orderStatus,totalAmount, orderDate, and a list of OrderItem objects.
* It establishes relationships with the User, Address, and OrderItem entities.
*/
@Entity
@Table(name = "orders") // "order" alone is a reserved SQL keyword - can't use it as a table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    // The unique identifier for the order.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * It represents the user who placed the order.
     * It establishes a many-to-one relationship with the User entity.
     * It connects the user_id column in the orders table to the id column in the users table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /*
     * It represents the address associated with the order.
     * It establishes a many-to-one relationship with the Address entity.
     * It connects the address_id column in the orders table to the id column in the addresses table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    /*
     * It represents the status of the order.
     * It is an enumerated type that can take values such as PENDING, CONFIRMED, SHIPPED, DELIVERED, or CANCELLED.
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    // It represents the total amount of the order.
    private BigDecimal totalAmount;

    // It represents the date and time when the order was placed.
    private LocalDateTime orderDate = LocalDateTime.now();

    // It represents the list of items in the order.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}