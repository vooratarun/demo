package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("product")
    @JsonIgnore
    private Set<OrderProduct> orderProducts = new HashSet<>();

    public Product() {}

    public Product(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    // Getters & setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public Set<OrderProduct> getOrderProducts() { return orderProducts; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(Double price) { this.price = price; }
    public void setOrderProducts(Set<OrderProduct> orderProducts) { this.orderProducts = orderProducts; }
}
