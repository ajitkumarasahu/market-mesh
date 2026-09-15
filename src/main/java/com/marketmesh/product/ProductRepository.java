package com.marketmesh.product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /*
     * Finds products by their category ID.
     */
    List<Product> findByCategoryId(Long categoryId);

    /*
     * Finds products by name, ignoring case.
     */
    List<Product> findByNameContainingIgnoreCase(String keyword);

    /*
     * Finds products by brand, ignoring case.
     */
    List<Product> findByBrandIgnoreCase(String brand);
}