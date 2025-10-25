package com.example.demo.controllers;

import com.example.demo.entity.Product;
import com.example.demo.repo.ProductRepository;
import com.example.demo.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Manage products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }
//
//
//    @Operation(summary = "Create a new product")
//    @PostMapping
//    public Product createProduct(@RequestBody Product product) {
//        return productRepository.save(product);
//    }
//
//    @Operation(summary = "Get all products")
//    @GetMapping
//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }

//    @Operation(summary = "Get product by ID")
//    @GetMapping("/{id}")
//    public Product getProductById(@PathVariable Long id) {
//        return productRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//    }

//    @Operation(summary = "Update product")
//    @PutMapping("/{id}")
//    public Product updateProduct(@PathVariable Long id, @RequestBody Product updated) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//        product.setName(updated.getName());
//        product.setPrice(updated.getPrice());
//        return productRepository.save(product);
//    }
//
//    @Operation(summary = "Delete product")
//    @DeleteMapping("/{id}")
//    public String deleteProduct(@PathVariable Long id) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//        productRepository.delete(product);
//        return "Product deleted successfully";
//    }
//
//    @Operation(summary = "Get all products of a orderId")
//    @GetMapping("/orders/{orderId}")
//    public List<Product> findProductsByOrderId(@PathVariable Long orderId) {
//        return productRepository.findProductsByOrderId(orderId);
//    }
//
//    @Operation(summary = "getTotalQuantitySold of a product")
//    @GetMapping("/total/{productId}")
//    public Integer getTotalQuantitySold(@PathVariable Long productId){
//        return productRepository.getTotalQuantitySold(productId);
//    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return service.getProductById(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return service.updateProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return service.updateProduct(product);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
        return "Product deleted successfully!";
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

}
