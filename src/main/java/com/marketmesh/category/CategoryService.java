package com.marketmesh.category;

import com.marketmesh.category.dto.CategoryRequest;
import com.marketmesh.category.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CategoryService handles category-related business operations.
 * It provides methods to create, retrieve, update, and delete categories.
 * It also manages parent-child category relationships and converts
 * category entities into response DTOs.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    /**
     * Repository used to access and manage category data in the database.
     * CategoryService uses this repository for CRUD operations and
     * for finding parent and top-level categories.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Creates a new category using the provided request data.
     * If a parent ID is provided, the corresponding parent category is
     * retrieved and assigned before saving the new category.
     */
    public CategoryResponse create(CategoryRequest request) {

        Category category = new Category();

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    /**
     * Retrieves all top-level categories that do not have a parent category.
     * Each category entity is converted into a CategoryResponse DTO,
     * including its child categories.
     */
    public List<CategoryResponse> getAllTopLevel() {
        return categoryRepository.findByParentIsNull()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves a category using its ID.
     * If the category does not exist, an exception is thrown.
     * The found entity is converted into a CategoryResponse DTO.
     */
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        return toResponse(category);
    }

    /**
     * Updates an existing category with the provided request data.
     * The parent category is also updated when a parent ID is provided;
     * otherwise, the category is changed to a top-level category.
     */
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return toResponse(categoryRepository.save(category));
    }

    /**
     * Deletes a category using its ID.
     * Before deletion, it checks whether the category exists.
     * An exception is thrown when the requested category is not found.
     */
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found");
        }

        categoryRepository.deleteById(id);
    }

    /**
     * Converts a Category entity into a CategoryResponse DTO.
     * It recursively converts child categories so the API can return
     * the complete category hierarchy without exposing raw entities.
     */
    private CategoryResponse toResponse(Category category) {
        List<CategoryResponse> children = category.getSubCategories() == null ? List.of() :
            category.getSubCategories().stream().map(this::toResponse).collect(Collectors.toList());

        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getParent() != null ? category.getParent().getId() : null,
            children
        );
    }
}