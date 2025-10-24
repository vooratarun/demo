package com.example.demo.repo;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT op.product FROM OrderProduct op WHERE op.order.id = :orderId")
    List<Product> findProductsByOrderId(@Param("orderId") Long orderId);


    @Query("SELECT SUM(op.quantity) FROM OrderProduct op WHERE op.product.id = :productId")
    Integer getTotalQuantitySold(@Param("productId") Long productId);

    @Query(value = """
   SELECT p.id AS product_id, p.name, SUM(op.quantity) AS total_sold
   FROM order_product op
   JOIN product p ON op.product_id = p.id
   GROUP BY p.id, p.name
   ORDER BY total_sold DESC
   """, nativeQuery = true)
    List<Object[]> findTopProductsBySalesNative();
}
