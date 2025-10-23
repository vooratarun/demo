package com.example.demo.controllers;

import com.example.demo.entity.Customer;
import com.example.demo.repo.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Manage customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Operation(summary = "Create a new customer")
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @Operation(summary = "Get all customers")
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Operation(summary = "Get customer by ID")
    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Operation(summary = "Update customer")
    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer updated) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setName(updated.getName());
        customer.setEmail(updated.getEmail());
        return customerRepository.save(customer);
    }

    @Operation(summary = "Delete customer")
    @DeleteMapping("/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customerRepository.delete(customer);
        return "Customer deleted successfully";
    }
}
