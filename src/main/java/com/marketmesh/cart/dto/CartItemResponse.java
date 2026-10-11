package com.marketmesh.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * CartItemResponse is a DTO that represents the response payload for an item in the cart.
 * It contains details about the product, such as ID, name, image, price, quantity, and subtotal.
 * This DTO is used to send cart item information back to the client in a structured format.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long productId;
    private String productName;
    private String productImage; // first image, for display in the cart UI
    private BigDecimal price;     // price snapshot at time of adding
    private int quantity;
    private BigDecimal subtotal;  // price * quantity
}