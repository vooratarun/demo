package com.example.demo.repo;

import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    List<Order> findOrdersByCustomerId(@Param("customerId") Long customerId);


    @Query("SELECT o FROM Order o JOIN FETCH o.address")
    List<Order> findAllOrdersWithAddress();

    @Query("""
       SELECT o FROM Order o
       JOIN o.address a
       WHERE a.city = :city
       """)
    List<Order> findOrdersByCity(@Param("city") String city);


    @Query("""
       SELECT o.customer.name, COUNT(o)
       FROM Order o
       GROUP BY o.customer.name
       """)
    List<Object[]> countOrdersPerCustomer();

    @Query("""
       SELECT o FROM Order o
       JOIN FETCH o.customer c
       JOIN FETCH o.address a
       """)
    List<Order> fetchOrdersWithCustomerAndAddress();

    @Query(value = """
   SELECT c.name AS customer_name, COUNT(o.id) AS total_orders
   FROM orders o
   JOIN customer c ON o.customer_id = c.id
   GROUP BY c.name
   """, nativeQuery = true)
    List<Object[]> countOrdersPerCustomerNative();
}

