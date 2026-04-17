package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZendeskTicketResponse {
    private Long id;
    private String subject;
    private String description;
    private String status;
    private String priority;
    private String type;
    private ZendeskRequester requester;
    private ZendeskAssignee assignee;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZendeskRequester {
        private Long id;
        private String name;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZendeskAssignee {
        private Long id;
        private String name;
        private String email;
    }
}

