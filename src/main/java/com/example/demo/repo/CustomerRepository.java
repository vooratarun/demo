package com.example.demo.repo;

import com.example.demo.entity.Address;
import com.example.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c JOIN FETCH c.addresses")
    List<Customer> findAllCustomersWithAddresses();


    @Query("SELECT DISTINCT o.customer FROM Order o")
    List<Customer> findCustomersWithOrders();

    @Query(value = "SELECT * FROM customers WHERE email = :email", nativeQuery = true)
    Customer findByEmail(@Param("email") String email);


    @Query(value = """
   SELECT DISTINCT c.*
   FROM customer c
   JOIN orders o ON o.customer_id = c.id
   WHERE o.total_amount > 10000
   """, nativeQuery = true)
    List<Customer> findHighValueCustomers();

}
