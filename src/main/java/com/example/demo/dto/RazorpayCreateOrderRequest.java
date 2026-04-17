package com.example.demo.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RazorpayCreateOrderRequest {
    /**
     * Amount in smallest currency unit (paise for INR).
     */
    private Long amount;
    private String currency = "INR";
    private String receipt;
    private Map<String, String> notes;
}
