package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderDate;

//    private String customerName;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("order")
    @JsonIgnore
    private Set<OrderProduct> orderProducts = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties("orders") // prevent recursion
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    @JsonIgnoreProperties({"orders", "customer"})
    private Address address;

    public Order() {}

    public Order(LocalDateTime orderDate, Customer customer) {
        this.orderDate = orderDate;
        this.customer = customer;
    }

    // Getters & setters
    public Long getId() { return id; }
    public LocalDateTime getOrderDate() { return orderDate; }
  //  public String getCustomerName() { return customerName; }
    public Set<OrderProduct> getOrderProducts() { return orderProducts; }

    public void setId(Long id) { this.id = id; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
   // public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setOrderProducts(Set<OrderProduct> orderProducts) { this.orderProducts = orderProducts; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}
