package com.marketmesh.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO used to return category information through the API.
 * It contains the category details, parent category ID, and
 * a list of child categories to represent the category hierarchy.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private List<CategoryResponse> subCategories;
}