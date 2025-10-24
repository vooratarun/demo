package com.example.demo.repo;

import com.example.demo.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomerId(Long customerId);


    // JPQL to fetch all addresses for a specific customer
    @Query("SELECT a FROM Address a WHERE a.customer.id = :customerId")
    List<Address> findAllByCustomerId(Long customerId);

    @Query(value = "SELECT * FROM address WHERE customer_id = :customerId", nativeQuery = true)
    List<Address> findAllByAddressCustomerId(@Param("customerId") Long customerId);

    @Query(value = """
   SELECT c.id AS customer_id, c.name AS customer_name, c.email, 
          a.id AS address_id, a.street, a.city, a.state, a.zipcode
   FROM customer c
   LEFT JOIN address a ON c.id = a.customer_id
   """, nativeQuery = true)
    List<Object[]> findAllCustomersWithAddresses();


}
