//package com.marketmesh.cart;
package com.marketmesh.cart;

/**
 * Represents a shopping cart in the system.
 * use import com.marketmesh.user.User;
 * use import java.time.LocalDateTime;
 * use import jakarta.persistence.*;
 * use import jakarta.validation.constraints.*;
 */
import com.marketmesh.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*  
 * tabale name: carts
 * it contains the following fields:
 * id: Long, primary key, auto-generated
 */
@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    //id: Long, primary key, auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*IN THIS TABALE COLUMN NAME IS user_id (FOREIGN KEY) 
    * it is a one-to-one relationship with the User entity
    * IT S CONNECTED TO THE USER ENTITY USING THE user_id COLUMN
    */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // List of items in the cart
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    private LocalDateTime updatedAt = LocalDateTime.now();
}