package com.example.demo.services;

import com.example.demo.entity.Product;
import com.example.demo.repo.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // --------------------------
    // Cache product by ID
    // --------------------------
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        System.out.println("Fetching product from DB...");
        return repository.findById(id).orElse(null);
    }

    // --------------------------
    // Update product and cache
    // --------------------------
    @CachePut(value = "products", key = "#product.id")
    public Product updateProduct(Product product) {
        return repository.save(product);
    }

    // --------------------------
    // Delete product and evict cache
    // --------------------------
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }

    // --------------------------
    // List all products (no caching here)
    // --------------------------
    public List<Product> getAllProducts() {
        return repository.findAll();
    }
}
