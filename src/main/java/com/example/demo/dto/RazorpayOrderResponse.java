package com.example.demo.dto;

import lombok.Data;

@Data
public class RazorpayOrderResponse {
    private String id;
    private String entity;
    private Long amount;
    private Long amountPaid;
    private Long amountDue;
    private String currency;
    private String receipt;
    private String status;
    private Integer attempts;
    private Long createdAt;
}
