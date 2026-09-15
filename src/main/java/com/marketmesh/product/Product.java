// package declaration
package com.marketmesh.product;

/*
 * Product entity representing a product in the system.
 * products have attributes such as name, description, price, stock quantity, brand, and category.
 * products can have multiple images associated with them, represented by the ProductImage entity.
 * jrkate validation annotations are used to enforce constraints on the fields, such as not blank for name 
 * and positive or zero for price and stock quantity.
 */
import com.marketmesh.category.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 *products table it contains fields for 
 *id, name, description, price, discount price, stock quantity, brand, category, images, and created at timestamp.
 * The id field is the primary key and is generated automatically.
 */
@Entity
@Table(name = "products") 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    // The id field is the primary key and is generated automatically.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The name field is a required field and cannot be blank.
    @NotBlank
    private String name;

    // The description field is optional and can be up to 2000 characters long.
    @Column(length = 2000)
    private String description;

    // The price field is a required field and must be positive or zero.
    @PositiveOrZero
    private BigDecimal price;

    // The discountPrice field is optional and can be null.
    private BigDecimal discountPrice;

    // The stockQuantity field is a required field and must be positive or zero.
    @PositiveOrZero
    private Integer stockQuantity = 0;

    // The brand field is optional and can be null.
    private String brand;

    /*COLUMN NAME IS category_id(FRORGIN KEY).
    * IT S CONTECT FOR CATEGORIES TABALE ID (THIS IS PRIMARY KEY)
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // The images field is a list of ProductImage entities associated with the product.
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();
}