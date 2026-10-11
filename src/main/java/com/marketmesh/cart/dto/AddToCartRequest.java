package com.marketmesh.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AddToCartRequest is a DTO that represents the request payload for adding an item to the cart.
 * It contains the product ID and the quantity of the product to be added.
 * Validation annotations ensure that the product ID is provided and that the quantity is at least 1.
 */
@Data
public class AddToCartRequest {

    // Product ID must be required
    @NotNull
    @Size(message = "Product ID must be required")
    private Long productId;

    // Quantity must be at least 1
    @NotNull
    @Min(1)
    @Size(min = 1, message = "Quantity must be at least 1")
    private int quantity;
}