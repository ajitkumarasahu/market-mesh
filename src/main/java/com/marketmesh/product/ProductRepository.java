package com.marketmesh.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    /**
     * Searches products using optional filter criteria such as category, category name,
     * price, brand, and product name keyword. Any filter with a null value is ignored.
     * Category name, brand, and keyword searches are case-insensitive, while the
     * keyword performs a partial match against the product name.
     */
    @Query("""
            SELECT p FROM Product p
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:categoryName IS NULL OR LOWER(p.category.name) = LOWER(:categoryName))
            AND (:price IS NULL OR p.price = :price)
            AND (:brand IS NULL OR LOWER(p.brand) = LOWER(:brand))
            AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))""")
                    
        
    List<Product> searchProducts(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            @Param("categoryName") String categoryName,
            @Param("price") BigDecimal price,
            @Param("brand") String brand);
}