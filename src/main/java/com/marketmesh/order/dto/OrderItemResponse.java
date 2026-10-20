package com.marketmesh.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO used to return individual order item details in API responses.
 * It contains product information, the price recorded at order time, and quantity.
 * The subtotal represents the total price for the ordered quantity of a product.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long productId;
    private String productName;
    private BigDecimal price;      // frozen price at order time
    private int quantity;
    private BigDecimal subtotal;   // price * quantity
}