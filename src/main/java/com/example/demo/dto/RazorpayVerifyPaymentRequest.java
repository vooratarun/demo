package com.example.demo.dto;

import lombok.Data;

@Data
public class RazorpayVerifyPaymentRequest {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
