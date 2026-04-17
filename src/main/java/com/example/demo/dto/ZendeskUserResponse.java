package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZendeskUserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private boolean verified;
    private boolean suspended;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime lastLoginAt;
}

