package com.example.demo.dto;

public class CustomerOrderCountDTO {
    private String customerName;
    private Long orderCount;

    public CustomerOrderCountDTO(String customerName, Long orderCount) {
        this.customerName = customerName;
        this.orderCount = orderCount;
    }

    public String getCustomerName() { return customerName; }
    public Long getOrderCount() { return orderCount; }
}
