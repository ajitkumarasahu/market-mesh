package com.marketmesh.order.dto;

import com.marketmesh.address.dto.AddressResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO used to return order details in API responses.
 * It includes customer information, order status, total amount, and order date.
 * It also contains shipping address details and the list of ordered items.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String customerEmail;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private AddressResponse shippingAddress;
    private List<OrderItemResponse> items;
}