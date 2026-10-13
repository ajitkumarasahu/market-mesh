package com.marketmesh.cart;

import com.marketmesh.cart.dto.AddToCartRequest;
import com.marketmesh.cart.dto.CartItemResponse;
import com.marketmesh.cart.dto.CartResponse;
import com.marketmesh.cart.dto.UpdateCartItemRequest;
import com.marketmesh.product.Product;
import com.marketmesh.product.ProductRepository;
import com.marketmesh.user.User;
import com.marketmesh.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling shopping cart business operations.
 * It manages adding, retrieving, updating, removing, and clearing cart items.
 * It also calculates item subtotals and the overall cart total.
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves the cart associated with the user's email address.
     * If the user does not have a cart, a new one is created and saved.
     * Throws an exception if the user does not exist.
     */
    // Every user has exactly one cart - create it on first use, reuse after that
    private Cart getOrCreateCart(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
                
        return cartRepository.findByUserId(user.getId())
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    /**
     * Adds a product to the user's shopping cart.
     * Checks product availability and increases the quantity if the product
     * already exists in the cart; otherwise, creates a new cart item.
     */
    public CartResponse addItem(String email, AddToCartRequest request) {
        Cart cart = getOrCreateCart(email);

        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product not found"));
                
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        // If this product is already in the cart, just increase quantity instead of duplicating
        CartItem existingItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(product.getId()))
            .findFirst()
            .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            // Snapshot the current price (uses discount price if present)
            newItem.setPrice(product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice());
            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    /**
     * Retrieves the shopping cart associated with the user's email address.
     * Creates a cart if one does not already exist for the user.
     * Returns the cart details, including items, quantities, and total price.
     */
    public CartResponse getCart(String email) {
        Cart cart = getOrCreateCart(email);
        return toResponse(cart);
    }

    /**
     * Updates the quantity of an existing cart item.
     * Checks product stock availability before applying the requested quantity.
     * Saves the updated cart and returns the refreshed cart response.
     */
    public CartResponse updateItem(String email, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(email);

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (item.getProduct().getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        item.setQuantity(request.getQuantity());
        cart.setUpdatedAt(LocalDateTime.now());

        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    /**
     * Removes a specific item from the user's shopping cart.
     * Throws an exception if the requested cart item does not exist.
     * Saves the updated cart and returns the remaining cart details.
     */
    public CartResponse removeItem(String email, Long itemId) {
        Cart cart = getOrCreateCart(email);

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new IllegalArgumentException("Cart item not found");
        }

        cart.setUpdatedAt(LocalDateTime.now());
        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    /**
     * Removes all items from the user's shopping cart.
     * Retrieves the user's cart and clears its item collection.
     * Saves the cart after removing the items.
     */
    public void clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    /**
     * Converts a Cart entity into a CartResponse DTO.
     * Maps cart items with product details, prices, quantities, and subtotals.
     * Calculates the total cart price and the total number of items.
     */
    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
            .map(item -> new CartItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getImages().isEmpty() ? null : item.getProduct().getImages().get(0).getImageUrl(),
                item.getPrice(),
                item.getQuantity(),
                item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            ))
        .collect(Collectors.toList());

        BigDecimal total = itemResponses.stream().map(CartItemResponse::getSubtotal) .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        int totalItems = itemResponses.stream().mapToInt(CartItemResponse::getQuantity).sum();

        return new CartResponse(cart.getId(), itemResponses, total, totalItems);
    }
}