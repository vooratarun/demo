package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class SqsMessageRequest {

    @NotBlank(message = "message is required")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
