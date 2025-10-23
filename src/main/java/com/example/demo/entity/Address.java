package com.example.demo.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "addresses")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    private String city;
    private String state;
    private String zipcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties("addresses")
    private Customer customer;

    public Address() {}

    public Address(String street, String city, String state, String zipcode, Customer customer) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.customer = customer;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipcode() { return zipcode; }
    public Customer getCustomer() { return customer; }

    public void setId(Long id) { this.id = id; }
    public void setStreet(String street) { this.street = street; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
