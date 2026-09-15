//package declaration for the ProductImage class, which is part of the com.marketmesh.product package.
package com.marketmesh.product;

/*
 * ProductImage entity representing an image associated with a product in the system.
 * Each ProductImage has attributes such as id, product, imageUrl, and displayOrder.
 * The product field is a many-to-one relationship with the Product entity, indicating that multiple images can be associated with a single product.
 * The imageUrl field is a required field and cannot be blank.
 * The displayOrder field is used to determine the order in which images are displayed for a product.
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/*
 * product_images table it contains fields for 
 * id, product_id, image_url, and display_order.
 * The id field is the primary key and is generated automatically.
 */
@Entity
@Table(name = "product_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    // The id field is the primary key and is generated automatically.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*COLUMN NAME IS product_id(FRORGIN KEY).
    * IT S CONTECT FOR PRODUCTS TABALE ID (THIS IS PRIMARY KEY)
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    // The imageUrl field is a required field and cannot be blank.
    @NotBlank
    private String imageUrl;

    // The displayOrder field is used to determine the order in which images are displayed for a product.
    private int displayOrder = 0;
}