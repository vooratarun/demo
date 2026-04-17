package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZendeskTicketRequest {
    private String subject;
    private String description;
    private ZendeskRequester requester;
    private String priority; // low, normal, high, urgent
    private String type; // question, incident, problem, task
    private String status; // new, open, pending, hold, solved, closed

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZendeskRequester {
        private String name;
        private String email;
    }
}

