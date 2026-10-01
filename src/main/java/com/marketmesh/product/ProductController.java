package com.marketmesh.product;

import com.marketmesh.common.ApiResponse;
import com.marketmesh.product.dto.ProductRequest;
import com.marketmesh.product.dto.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for managing product-related API requests.
 * It provides endpoints for creating, retrieving, updating, and deleting products.
 * It also supports filtering products using category, keyword, price, brand,
 * and category name.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    /**
     * Service used to perform product-related business operations.
     * The service is injected through constructor injection using Lombok's
     * @RequiredArgsConstructor annotation.
     */
    private final ProductService productService;

    /**
     * Creates a new product using the provided request data.
     * The request is validated before being passed to the service layer.
     * Returns the created product with HTTP 201 CREATED status.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Product created successfully", response));       
    }

    // @GetMapping
    // public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll(
    //     @RequestParam(required = false) Long categoryId,
    //     @RequestParam(required = false) String keyword) {

    //     List<ProductResponse> products;
    //     if (keyword != null && !keyword.isBlank()) {
    //         products = productService.search(keyword);
    //     } else if (categoryId != null) {
    //         products = productService.getByCategory(categoryId);
    //     } else {
    //         products = productService.getAll();
    //     }

    //     return ResponseEntity.ok(ApiResponse.success("Products fetched successfully", products));
    // }

    /**
     * Retrieves products using optional filter parameters.
     * Supports filtering by category ID, category name, keyword, price, and brand.
     * Returns the matching products with HTTP 200 OK status.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String categoryName,
        @RequestParam(required = false) BigDecimal price,
        @RequestParam(required = false) String brand) {

        List<ProductResponse> products = productService.getProducts(
            categoryId,
            keyword,
            categoryName,
            price,
            brand
        );

        return ResponseEntity.ok(
            ApiResponse.success("Products fetched successfully", products)
        );
    }

    /**
     * Retrieves a single product using its ID.
     * The ID is received from the URL path and passed to the service layer.
     * Returns the product details with HTTP 200 OK status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        ProductResponse response = productService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Product fetched successfully", response));
    }

    /**
     * Updates an existing product using its ID and request data.
     * The request body is validated before being processed by the service.
     * Returns the updated product with HTTP 200 OK status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", response));
    }

    /**
     * Deletes an existing product using its ID.
     * The delete operation is handled by the service layer.
     * Returns a success response with HTTP 200 OK status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}