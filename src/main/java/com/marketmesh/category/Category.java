//package declaration for the Category class, indicating its location within the project structure.
package com.marketmesh.category;

/**
 * Category entity representing a product category.
 * jarkarta.persistence.Entity is used to mark this class as a JPA entity.
 * jarkarta.persistence.Table is used to specify the table name in the database.
 * lombok.Data generates getters, setters, toString, equals, and hashCode methods.
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/*
 * Category entity representing a product category.
 * This class is annotated with JPA annotations to map it to a database table.
 * It includes fields for category details such as name, description, parent category, and subcategories.
 * The parent field is annotated with @ManyToOne to establish a many-to-one relationship with the Category entity itself.
 * The subCategories field is annotated with @OneToMany to establish a one-to-many relationship with the Category entity itself.
 * The @JsonIgnore annotation is used to prevent serialization of the parent field when converting the Category object to JSON.
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
       
    // Primary key for the Category entity, automatically generated.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name of the category, cannot be blank.
    @NotBlank
    private String name;

    // Description of the category, can be blank.
    private String description;

    // Many-to-one relationship with the Category entity itself, indicating that a category can have a parent category.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Category parent;

    // One-to-many relationship with the Category entity itself, indicating that a category can have multiple subcategories.
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> subCategories = new ArrayList<>();
}