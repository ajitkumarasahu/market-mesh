package com.marketmesh.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {


    /*
     * Finds all orders placed by a specific user, ordered by the order date in descending order.
     *
     * @param userId The ID of the user whose orders are to be retrieved.
     * @return A list of orders placed by the specified user, ordered by order date in descending order.
     */
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
}