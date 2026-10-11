package com.marketmesh.cart;

import com.marketmesh.cart.dto.AddToCartRequest;
import com.marketmesh.cart.dto.CartItemResponse;
import com.marketmesh.cart.dto.CartResponse;
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

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartResponse addItem(String email, AddToCartRequest request) {
        Cart cart = getOrCreateCart(email);

        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product are not found"));
                
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
            newItem.setPrice(product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice());
            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    public CartResponse getCart(String email) {
        Cart cart = getOrCreateCart(email);
        return toResponse(cart);
    }

    public CartResponse updateItemQuantity(String email, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(email);

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        item.setQuantity(quantity);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return toResponse(cart);
    }

    public CartResponse removeItem(String email, Long itemId) {
        Cart cart = getOrCreateCart(email);

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new IllegalArgumentException("Cart item not found");
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return toResponse(cart);
    }

    public void clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // Every logged-in user gets exactly one cart - created lazily on first use
    private Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
                
        return cartRepository.findByUserId(user.getId())
            .orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.toList());

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cart.getId(), itemResponses, total);
    }
}