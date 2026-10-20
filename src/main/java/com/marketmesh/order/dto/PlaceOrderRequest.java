package com.marketmesh.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO used to receive the details required to place an order.
 * It identifies the delivery address selected by the customer.
 * Validation ensures that an address ID is provided in the request.
 */
@Data
public class PlaceOrderRequest {

    @NotNull
    private Long addressId;
}