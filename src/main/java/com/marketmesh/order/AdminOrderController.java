package com.marketmesh.order;

import com.marketmesh.common.ApiResponse;
import com.marketmesh.order.dto.OrderResponse;
import com.marketmesh.order.dto.UpdateOrderStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for administrative order management operations.
 * It provides endpoints to retrieve all customer orders and update order statuses.
 * Requests are processed through the OrderService business layer.
 */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    /** Service responsible for retrieving orders and managing order status changes. */
    private final OrderService orderService;

    /**
     * Retrieves all orders available in the system.
     * Delegates order retrieval to the service layer.
     * Returns the order list with HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> response = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success("All orders fetched Successfully", response));
    }

    /**
     * Updates the status of a specific order.
     * Validates the request and delegates status transition handling to the service layer.
     * Returns the updated order details with HTTP status 200 (OK).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable Long id,@Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Order status updated Successfully", response));
    }
}