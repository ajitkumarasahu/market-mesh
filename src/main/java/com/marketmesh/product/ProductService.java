package com.marketmesh.product;

import com.marketmesh.category.Category;
import com.marketmesh.category.CategoryRepository;
import com.marketmesh.product.dto.ProductRequest;
import com.marketmesh.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling product-related business operations.
 * It manages product creation, retrieval, updating, and deletion.
 * It also handles product categories and product image information.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    /**
     * Repository used to perform product database operations.
     * CategoryRepository is used to find and validate categories
     * associated with products.
     */
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Creates a new product using the provided request data.
     * It validates the category, sets product details, and creates
     * product images before saving the product to the database.
     */
    public ProductResponse create(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("Category Id not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);
        product.setBrand(request.getBrand());
        product.setCategory(category);

        if (request.getImageUrls() != null) {
            List<ProductImage> images = new ArrayList<>();
            int order = 0;
            for (String url : request.getImageUrls()) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(url);
                image.setDisplayOrder(order++);
                images.add(image);
            }
            product.setImages(images);
        }

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    /**
     * Retrieves all products from the database.
     * Each product entity is converted into a ProductResponse DTO
     * before being returned to the controller.
     */
    public List<ProductResponse> getAll() {
        return productRepository.findAll()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves products using optional search and filter criteria.
     * Filters can include category ID, keyword, category name, price,
     * and brand, and the results are converted into response DTOs.
     */
    public List<ProductResponse> getProducts(
        Long categoryId,
        String keyword,
        String categoryName,
        BigDecimal price,
        String brand) {

        return productRepository
            .searchProducts(categoryId, keyword, categoryName, price, brand)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all products belonging to a specific category.
     * The products are fetched using the category ID and converted
     * into ProductResponse DTOs before returning the result.
     */
    public List<ProductResponse> getByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Searches products by their name using a case-insensitive keyword.
     * The matching products are retrieved from the repository and
     * converted into ProductResponse DTOs.
     */
    public List<ProductResponse> search(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves a product using its ID.
     * If the product does not exist, an exception is thrown.
     * The found product is then converted into a ProductResponse DTO.
     */
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));        
        return toResponse(product);
    }

    /**
     * Updates an existing product using its ID and request data.
     * It also validates the product category and replaces the existing
     * product images when new image URLs are provided.
     */
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
                

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("Category not found"));
                
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : product.getStockQuantity());
        product.setBrand(request.getBrand());
        product.setCategory(category);

        // Replace images entirely on update - simplest approach for now
        if (request.getImageUrls() != null) {
            product.getImages().clear();
            int order = 0;
            for (String url : request.getImageUrls()) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(url);
                image.setDisplayOrder(order++);
                product.getImages().add(image);
            }
        }

        return toResponse(productRepository.save(product));
    }

    /**
     * Deletes a product using its ID.
     * Before deletion, it checks whether the product exists.
     * An exception is thrown when the requested product is not found.
     */
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(id);
    }

    /**
     * Converts a Product entity into a ProductResponse DTO.
     * It extracts and sorts product image URLs by display order
     * and includes the related category information in the response.
     */
    private ProductResponse toResponse(Product product) {
        List<String> urls = product.getImages() == null ? List.of() :
            product.getImages().stream()
                .sorted((a, b) -> a.getDisplayOrder() - b.getDisplayOrder())
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        return new ProductResponse(
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getDiscountPrice(),
            product.getStockQuantity(),
            product.getBrand(),
            product.getCategory().getId(),
            product.getCategory().getName(),
            urls
        );
    }
}