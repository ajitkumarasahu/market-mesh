package com.marketmesh.category;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * CategoryRepository interface for performing CRUD operations on Category entities.
 * Extends JpaRepository to provide standard database operations.
 * Includes custom query methods to find categories based on their parent category.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);
}