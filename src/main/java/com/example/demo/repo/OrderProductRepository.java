package com.example.demo.repo;

import com.example.demo.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByOrderId(Long orderId);
    List<OrderProduct> findByProductId(Long productId);

    @Query("""
       SELECT op.product, SUM(op.quantity) AS totalSold
       FROM OrderProduct op
       GROUP BY op.product
       ORDER BY totalSold DESC
       """)
    List<Object[]> findTopProductsBySales();
}
