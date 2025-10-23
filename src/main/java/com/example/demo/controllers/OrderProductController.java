package com.example.demo.controllers;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderProduct;
import com.example.demo.entity.Product;
import com.example.demo.repo.OrderProductRepository;
import com.example.demo.repo.OrderRepository;
import com.example.demo.repo.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-products")
@Tag(name = "OrderProducts", description = "Manage products within orders")
public class OrderProductController {

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Operation(summary = "Add product to order with quantity")
    @PostMapping("/order/{orderId}/product/{productId}")
    public OrderProduct addProductToOrder(
            @PathVariable Long orderId,
            @PathVariable Long productId,
            @RequestParam int quantity
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if already exists
        OrderProduct existing = orderProductRepository.findByOrderId(orderId)
                .stream()
                .filter(op -> op.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
        if (existing != null) {
            existing.setQuantity(quantity); // update quantity
            return orderProductRepository.save(existing);
        }

        OrderProduct orderProduct = new OrderProduct(order, product, quantity);
        return orderProductRepository.save(orderProduct);
    }

    @Operation(summary = "Remove product from order")
    @DeleteMapping("/order/{orderId}/product/{productId}")
    public String removeProductFromOrder(
            @PathVariable Long orderId,
            @PathVariable Long productId
    ) {
        OrderProduct orderProduct = orderProductRepository.findByOrderId(orderId)
                .stream()
                .filter(op -> op.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
        if (orderProduct == null) return "Product not found in order";

        orderProductRepository.delete(orderProduct);
        return "Product removed from order successfully";
    }

    @Operation(summary = "Get all products in an order")
    @GetMapping("/order/{orderId}")
    public List<OrderProduct> getProductsInOrder(@PathVariable Long orderId) {
        return orderProductRepository.findByOrderId(orderId);
    }

    @Operation(summary = "Get all orders containing a product")
    @GetMapping("/product/{productId}")
    public List<OrderProduct> getOrdersForProduct(@PathVariable Long productId) {
        return orderProductRepository.findByProductId(productId);
    }
}
