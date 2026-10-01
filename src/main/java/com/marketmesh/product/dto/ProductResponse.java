package com.marketmesh.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO used to return product information through the API.
 * It contains product details, pricing, stock, brand, and category information.
 * It also includes the list of image URLs associated with the product.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stockQuantity;
    private String brand;
    private Long categoryId;
    private String categoryName;
    private List<String> imageUrls;
}