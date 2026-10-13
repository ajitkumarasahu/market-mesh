package com.marketmesh.cart.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * UpdateCartItemRequest is a DTO that represents the request payload for updating the quantity of an item in the cart.
 * It contains the new quantity for the cart item.
 * Validation annotations ensure that the quantity is at least 1.
 */
@Data
public class UpdateCartItemRequest {

    @Min(1)
    private int quantity;
}