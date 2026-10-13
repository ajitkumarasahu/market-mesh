package com.marketmesh.cart;

import com.marketmesh.cart.dto.AddToCartRequest;
import com.marketmesh.cart.dto.CartResponse;
import com.marketmesh.cart.dto.UpdateCartItemRequest;
import com.marketmesh.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * CartController handles all cart-related operations such as a 
 * adding items, updating items, removing items, and clearing the cart.
 * It uses the CartService to perform business logic and returns appropriate responses to the client.
 * The controller is secured, and the user's email is extracted from the JWT token for all operations.
 * All endpoints return a standardized ApiResponse object containing the status, message, and data.
 */

//rest controller for cart operations
@RestController
//path for cart operations
@RequestMapping("/api/cart")
@RequiredArgsConstructor
/*
 *all methods in this controller are secured and require authentication.
 */
public class CartController {

    //call the cart service to perform business logic
    private final CartService cartService;

    /**
     * Get the current user's cart.
     *
     * @param authentication The authentication object containing the user's details.
     * @return ResponseEntity containing the ApiResponse with the CartResponse data.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication authentication) {

        String email = authentication.getName(); // comes from the JWT token, never from the client body
        CartResponse response = cartService.getCart(email);

        return ResponseEntity.status(HttpStatus.CREATED)
           .body(ApiResponse
           .success("Cart fetched Successfully", response
        ));
    }

    /**
     * Add an item to the current user's cart.
     *
     * @param authentication The authentication object containing the user's details.
     * @param request        The AddToCartRequest containing product ID and quantity.
     * @return ResponseEntity containing the ApiResponse with the updated CartResponse data.
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(

        Authentication authentication, 
        @Valid @RequestBody 
        AddToCartRequest request) { 

        String email = authentication.getName();
        CartResponse response = cartService.addItem(email, request);

        return ResponseEntity.status(HttpStatus.CREATED)
           .body(ApiResponse
           .success("Item added to cart Successfully", response
        ));
    }

    /**
     * Update the quantity of an item in the current user's cart.
     *
     * @param authentication The authentication object containing the user's details.
     * @param itemId         The ID of the cart item to update.
     * @param request        The UpdateCartItemRequest containing the new quantity.
     * @return ResponseEntity containing the ApiResponse with the updated CartResponse data.
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(

        Authentication authentication,
        @PathVariable Long itemId,
        @Valid @RequestBody UpdateCartItemRequest request) {
                                                                                                                           
        String email = authentication.getName();
        CartResponse response = cartService.updateItem(email, itemId, request);
         
        return ResponseEntity.status(HttpStatus.CREATED)
           .body(ApiResponse
           .success("Cart item updated Successfully", response
        ));
    }

    /**
     * Remove an item from the current user's cart.
     *
     * @param authentication The authentication object containing the user's details.
     * @param itemId         The ID of the cart item to remove.
     * @return ResponseEntity containing the ApiResponse with the updated CartResponse data.
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
        Authentication authentication,
        @PathVariable Long itemId) {

        String email = authentication.getName();
        CartResponse response = cartService.removeItem(email, itemId);

        return ResponseEntity.status(HttpStatus.CREATED)
           .body(ApiResponse
           .success("Item removed from cart Successfully", response
        ));
    }

    /**
     * Clear all items from the current user's cart.
     *
     * @param authentication The authentication object containing the user's details.
     * @return ResponseEntity containing the ApiResponse indicating the cart has been cleared.
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {

        String email = authentication.getName();
        cartService.clearCart(email);

        return ResponseEntity.status(HttpStatus.CREATED)
           .body(ApiResponse.success("Cart cleared Successfully", null
        ));
    }
}