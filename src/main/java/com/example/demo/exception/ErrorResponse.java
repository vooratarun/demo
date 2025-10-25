package com.example.demo.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private int status;

    public ErrorResponse(LocalDateTime timestamp, String message, String details, int status) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.status = status;
    }

    // Getters and setters omitted for brevity
}
