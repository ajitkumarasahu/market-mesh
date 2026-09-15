package com.marketmesh.cart;

/**
 * Represents an item in a shopping cart.
 * use import com.marketmesh.cart.Cart;
 * use import com.marketmesh.product.Product;
 * use import jakarta.persistence.*;
 * use import jakarta.validation.constraints.*;
 * use import java.math.BigDecimal;
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketmesh.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/*  
 * tabale name: cart_items
 * it contains the following fields:
 * id: Long, primary key, auto-generated
 * cart: Cart, many-to-one relationship with the Cart entity
 * product: Product, many-to-one relationship with the Product entity
 * quantity: int, minimum value of 1
 * price: BigDecimal, price of the product at the time of adding to the cart
 */
@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    //id: Long, primary key, auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    * IN THIS TABALE COLUMN NAME IS cart_id (FOREIGN KEY)
    * it is a many-to-one relationship with the Cart entity
    * IT S CONNECTED TO THE CART ENTITY USING THE cart_id COLUMN
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore
    private Cart cart;

    /*
    * IN THIS TABALE COLUMN NAME IS product_id (FOREIGN KEY)
    * it is a many-to-one relationship with the Product entity
    * IT S CONNECTED TO THE PRODUCT ENTITY USING THE product_id COLUMN
     */
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Min(1)
    private int quantity;

    private BigDecimal price;
}