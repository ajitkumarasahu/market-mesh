package com.marketmesh.cart;

import com.marketmesh.cart.dto.AddToCartRequest;
import com.marketmesh.cart.dto.CartResponse;
import com.marketmesh.cart.dto.UpdateCartItemRequest;
import com.marketmesh.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@Valid @RequestBody AddToCartRequest request,Authentication authentication) {

        CartResponse response = cartService.addItem(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart Successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication authentication) {
        CartResponse response = cartService.getCart(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Cart fetched Successfully", response));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem( @PathVariable Long itemId,@Valid @RequestBody UpdateCartItemRequest request,Authentication authentication) {

        CartResponse response = cartService.updateItemQuantity(authentication.getName(), itemId, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Cart item updated Successfully", response));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem( @PathVariable Long itemId,Authentication authentication) {

        CartResponse response = cartService.removeItem(authentication.getName(), itemId);
        return ResponseEntity.ok(ApiResponse.success("Cart item removed Successfully", response));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared Successfully", null));
    }
}