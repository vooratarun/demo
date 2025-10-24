package com.example.demo.controllers;

import com.example.demo.dto.AddressDTO;
import com.example.demo.entity.Address;
import com.example.demo.entity.Customer;
import com.example.demo.repo.AddressRepository;
import com.example.demo.repo.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Addresses", description = "Manage customer addresses")
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Operation(summary = "Create a new address for a customer")
    @PostMapping("/customer/{customerId}")
    public Address createAddress(@PathVariable Long customerId, @RequestBody Address address) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        address.setCustomer(customer);
        return addressRepository.save(address);
    }

    @Operation(summary = "Get all addresses of a customer")
    @GetMapping("/customer/{customerId}")
    public List<Address> getAddressesByCustomer(@PathVariable Long customerId) {
        return addressRepository.findByCustomerId(customerId);
    }

    @Operation(summary = "Get address by ID")
    @GetMapping("/{id}")
    public Address getAddressById(@PathVariable Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }

    @Operation(summary = "Delete address")
    @DeleteMapping("/{id}")
    public String deleteAddress(@PathVariable Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        addressRepository.delete(address);
        return "Address deleted successfully";
    }


    @GetMapping("{customerId}/address-list")
    public List<AddressDTO> getAllAddressesOfCustomer(@PathVariable Long customerId) {
        return addressRepository.findAllByCustomerId(customerId)
                .stream()
                .map(a -> new AddressDTO(a.getId(), a.getStreet(), a.getCity(), a.getState(), a.getZipcode()))
                .toList();
    }
}
