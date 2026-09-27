package com.marketmesh.category.dto;

/**
 * DTO for receiving category request data.
 */
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    // Category name.
    @NotBlank
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    // Category description.
    @NotBlank
    @Size(min = 2, max = 100, message = "Description must be between 2 and 100 characters")
    private String description;

    // Optional parent category ID. Null means top-level category.
    private Long parentId;
}