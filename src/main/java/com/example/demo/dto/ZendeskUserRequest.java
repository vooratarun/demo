package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZendeskUserRequest {
    private String name;
    private String email;
    private String phone;
    private String role; // end-user, agent, admin
    private boolean verified = false;
    private boolean suspended = false;
}

