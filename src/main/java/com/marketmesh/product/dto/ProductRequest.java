package com.marketmesh.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO used to receive product data from the client.
 * It contains product details, pricing, stock, category, and image information.
 * Validation annotations ensure that required fields contain valid values.
 */
@Data
public class ProductRequest {

    @NotBlank
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank
    @Size(min = 2, max = 100, message = "description must be between 2 and 100 characters")
    private String description;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    // Optional discount price. Null means no discount.
    private BigDecimal discountPrice;

    @PositiveOrZero
    private Integer stockQuantity;

    private String brand;

    @NotNull
    private Long categoryId;

    // List of image URLs associated with the product.
    private List<String> imageUrls;
}