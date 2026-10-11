package com.marketmesh.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {

    @Min(1)
    @NotNull(message = "Quantity must be at least 1")
    private int quantity;
}