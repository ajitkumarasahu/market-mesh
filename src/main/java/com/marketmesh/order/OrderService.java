package com.marketmesh.order;

import com.marketmesh.address.Address;
import com.marketmesh.address.AddressRepository;
import com.marketmesh.address.dto.AddressResponse;
import com.marketmesh.cart.Cart;
import com.marketmesh.cart.CartItem;
import com.marketmesh.cart.CartRepository;
import com.marketmesh.order.dto.OrderItemResponse;
import com.marketmesh.order.dto.OrderResponse;
import com.marketmesh.order.dto.PlaceOrderRequest;
import com.marketmesh.product.Product;
import com.marketmesh.user.User;
import com.marketmesh.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

// @Transactional makes every method all-or-nothing: if anything fails halfway
// (e.g. stock check on the 3rd item), nothing is saved - no half-created orders.

/**
 * Service responsible for customer and administrator order operations.
 * It handles order placement, retrieval, cancellation, status transitions, and stock updates.
 * Transaction management helps keep related database operations consistent when failures occur.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    /** Repository used to save orders and retrieve order information. */
    private final OrderRepository orderRepository;

    /** Repository used to retrieve and update customer shopping carts. */
    private final CartRepository cartRepository;

    /** Repository used to retrieve delivery addresses selected for orders. */
    private final AddressRepository addressRepository;

    /** Repository used to retrieve registered users by email address. */
    private final UserRepository userRepository;

    // ---------- USER operations ----------

    /**
     * Creates an order from the authenticated user's shopping cart.
     * Validates the cart, address ownership, and product stock before saving the order.
     * Updates stock quantities, calculates the total, and clears the cart after successful placement.
     */
    public OrderResponse placeOrder(String email, PlaceOrderRequest request) {
        User user = findUser(email);

        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new IllegalArgumentException("Your cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty");
        }

        // The address must exist AND belong to this user
        Address address = addressRepository.findById(request.getAddressId()).orElseThrow(() -> new IllegalArgumentException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address not found");
        }

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setOrderStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for: " + product.getName());
            }

            // Deduct stock (product is a managed entity, so this is saved automatically on commit)
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());

            // Copy cart line into a permanent order line, with the price frozen
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            order.getItems().add(orderItem);

            total = total.add(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);

        // Order placed, so empty the cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return toResponse(saved);
    }

    /**
     * Retrieves all orders placed by the specified user.
     * Orders are returned in descending order by order date.
     * Each order is converted into an OrderResponse DTO.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String email) {
        User user = findUser(email);
        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId())
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    }

    /**
     * Retrieves a single order belonging to the specified user.
     * The ownership check prevents access to another user's order.
     * Returns the order details as an OrderResponse DTO.
     */
    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(String email, Long orderId) {
        return toResponse(findOwnedOrder(email, orderId));
    }

    /**
     * Cancels an order owned by the specified user when its status allows cancellation.
     * Only orders with PENDING or CONFIRMED status can be cancelled.
     * Restores the reserved product quantities and returns the updated order details.
     */
    public OrderResponse cancelMyOrder(String email, Long orderId) {
        Order order = findOwnedOrder(email, orderId);

        OrderStatus status = order.getOrderStatus();
        if (status != OrderStatus.PENDING && status != OrderStatus.CONFIRMED) {
            throw new IllegalArgumentException("Order can no longer be cancelled (current status: " + status + ")");
        }

        cancelAndRestock(order);
        return toResponse(order);
    }

    // ---------- ADMIN operations ----------

    /**
     * Retrieves all orders for administrative purposes.
     * Orders are sorted by order date in descending order.
     * Each order is converted into an OrderResponse DTO.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate"))
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    }

    /**
     * Updates an order's status after validating the requested transition.
     * If the new status is CANCELLED, the reserved product stock is restored.
     * Returns the updated order as an OrderResponse DTO.
     */
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));

        OrderStatus current = order.getOrderStatus();
        if (!isValidTransition(current, newStatus)) {
            throw new IllegalArgumentException("Cannot change order status from " + current + " to " + newStatus);
        }

        if (newStatus == OrderStatus.CANCELLED) {
            cancelAndRestock(order);
        } else {
            order.setOrderStatus(newStatus);
        }

        return toResponse(order);
    }

    // ---------- helpers ----------

    // Allowed flow: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED, cancel allowed before shipping

    /**
     * Checks whether an order can move from its current status to the requested status.
     * Supports the defined order lifecycle and cancellation before shipment.
     * Returns false when the requested transition is not permitted.
     */
    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PENDING -> to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
            case CONFIRMED -> to == OrderStatus.SHIPPED || to == OrderStatus.CANCELLED;
            case SHIPPED -> to == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }

    // Cancelling puts the reserved stock back

    /**
     * Cancels an order and restores the quantities of its ordered products.
     * Each order item's quantity is added back to the corresponding product stock.
     * Finally, the order status is changed to CANCELLED.
     */
    private void cancelAndRestock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
    }

    /**
     * Finds a registered user by email address.
     * Returns the matching user when one exists.
     * Throws an IllegalArgumentException when the user cannot be found.
     */
    private User findUser(String email) {

        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Same "not found" message whether the order doesn't exist or belongs to someone else,
    // so nobody can discover other users' order IDs

    /**
     * Retrieves an order only when it belongs to the specified user.
     * Uses the same error message for missing orders and orders owned by another user.
     * This prevents users from discovering other customers' order IDs.
     */
    private Order findOwnedOrder(String email, Long orderId) {

        User user = findUser(email);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Order not found");
        }
        return order;
    }

    /**
     * Converts an Order entity into an OrderResponse DTO.
     * Maps order items, calculates each item's subtotal, and includes delivery address details.
     * Returns the order ID, customer email, status, total amount, date, address, and items.
     */
    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
        .map(item -> new OrderItemResponse(
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getPrice(),
            item.getQuantity(),
            item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        ))
        .collect(Collectors.toList());

        Address a = order.getAddress();
        AddressResponse address = new AddressResponse(
            // a.getId(),
            a.getStreet(),
            a.getCity(),
            a.getState(),
            a.getPincode(),
            a.getCountry(),
            a.isDefault()
        );

        return new OrderResponse(
            order.getId(),
            order.getUser().getEmail(),
            order.getOrderStatus().name(),
            order.getTotalAmount(),
            order.getOrderDate(),
            address,
            items
        );
    }
}