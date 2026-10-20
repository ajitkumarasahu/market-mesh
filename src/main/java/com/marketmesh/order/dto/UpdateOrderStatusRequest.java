package com.marketmesh.order.dto;

import com.marketmesh.order.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO used to receive order status update requests from administrators.
 * It specifies the new status to be assigned to an existing order.
 * Validation ensures that a status value is provided in the request.
 */
@Data
public class UpdateOrderStatusRequest {

    @NotNull
    private OrderStatus status;
}