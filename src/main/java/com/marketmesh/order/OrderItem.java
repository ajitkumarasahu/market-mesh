package com.marketmesh.order;

/*
 * import com.fasterxml.jackson.annotation.JsonIgnore;
 * import com.marketmesh.product.Product;
 * import jakarta.persistence.*;
 * import lombok.Data;
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketmesh.product.Product;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
/*
* Represents an item in an order.
* it contains information about the product, quantity, and price of the item.
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /*
     * The unique identifier for the order item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * IT represents the order to which this item belongs.
     * It establishes a many-to-one relationship with the Order entity.
     * IT CONECTS the order_id column in the order_items table to the id column in the orders table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    /*
     * It represents the product associated with this order item.
     * It establishes a many-to-one relationship with the Product entity.
     * It connects the product_id column in the order_items table to the id column in the products table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    private BigDecimal price;
}