package com.example.demo.controllers;

import com.example.demo.entity.Address;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Order;
import com.example.demo.repo.OrderRepository;
import com.example.demo.repo.CustomerRepository;
import com.example.demo.repo.AddressRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Manage orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;


    @Operation(summary = "Create a new order")
    @PostMapping
    public Order createOrder(@RequestParam Long  customerId,
                             @RequestParam Long addressId
    ) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Order order = new Order(LocalDateTime.now(), customer);

//

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        order.setCustomer(customer);
        order.setAddress(address);
        order.setOrderDate(LocalDateTime.now());
        return  order;
    }

    @Operation(summary = "Get all orders")
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Operation(summary = "Get order by ID")
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Operation(summary = "Delete order")
    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        orderRepository.delete(order);
        return "Order deleted successfully";
    }
}
