package com.marketmesh.category;

/**
 * CategoryController handles HTTP requests related to category management.
 * It provides endpoints for creating, retrieving, updating, and deleting categories.
 */
import com.marketmesh.category.dto.CategoryRequest;
import com.marketmesh.category.dto.CategoryResponse;
import com.marketmesh.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing categories.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    /**
     * Service for handling category-related operations.
     * categoryService is object of CategoryService class which is injected via constructor injection.
     * and call the methods of CategoryService class to perform the operations.
     */
    private final CategoryService categoryService;

    /**
     * Creates a new category.
     *
     * @param request the category request payload
     * @return ResponseEntity containing the created category response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Category created successfully", response));      
    }

    /**
     * Retrieves all top-level categories.
     *
     * @return ResponseEntity containing the list of top-level category responses
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        List<CategoryResponse> categories = categoryService.getAllTopLevel();
        return ResponseEntity.ok(ApiResponse.success("Categories fetched successfully", categories));
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category
     * @return ResponseEntity containing the category response
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Category fetched successfully", response));
    }

    /**
     * Updates an existing category.
     *
     * @param id      the ID of the category to update
     * @param request the category request payload
     * @return ResponseEntity containing the updated category response
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", response));
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id the ID of the category to delete
     * @return ResponseEntity indicating the success of the deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}