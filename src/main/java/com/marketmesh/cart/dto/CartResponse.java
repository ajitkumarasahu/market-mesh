package com.marketmesh.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * CartResponse is a DTO that represents the response payload for the user's cart.
 * It contains the cart ID, a list of CartItemResponse objects, the total amount, and the total number of items in the cart.
 * This DTO is used to send cart information back to the client in a structured format.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long cartId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private int totalItems;
}