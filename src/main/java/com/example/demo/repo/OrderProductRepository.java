package com.example.demo.repo;

import com.example.demo.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByOrderId(Long orderId);
    List<OrderProduct> findByProductId(Long productId);
}
