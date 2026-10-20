package com.marketmesh.order;

import com.marketmesh.common.ApiResponse;
import com.marketmesh.order.dto.OrderResponse;
import com.marketmesh.order.dto.PlaceOrderRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing customer order-related API requests.
 * It provides endpoints for placing orders, retrieving order details, and cancelling orders.
 * Authenticated user details are used to ensure orders are accessed by their owners.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    /** Service responsible for handling order operations and business logic. */
    private final OrderService orderService;

    /**
     * Places a new order for the authenticated user.
     * Validates the incoming request and delegates order processing to the service layer.
     * Returns the created order with HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(Authentication authentication,@Valid @RequestBody PlaceOrderRequest request) {

        OrderResponse response = orderService.placeOrder(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Order placed", response));
    }

    /**
     * Retrieves all orders belonging to the authenticated user.
     * Uses the authenticated user's identity to fetch their order history.
     * Returns the list of orders with HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(Authentication authentication) {
        List<OrderResponse> response = orderService.getMyOrders(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Orders fetched", response));
    }

    /**
     * Retrieves a specific order belonging to the authenticated user.
     * The order ID identifies the requested order, while authentication identifies its owner.
     * Returns the order details with HTTP status 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrder(Authentication authentication, @PathVariable Long id) {

        OrderResponse response = orderService.getMyOrder(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Order fetched", response));
    }

    /**
     * Cancels an order belonging to the authenticated user.
     * Delegates cancellation processing and validation to the order service.
     * Returns the updated order details with HTTP status 200 (OK).
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(Authentication authentication,@PathVariable Long id) {

        OrderResponse response = orderService.cancelMyOrder(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", response));
    }
}