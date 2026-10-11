package com.marketmesh.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
}