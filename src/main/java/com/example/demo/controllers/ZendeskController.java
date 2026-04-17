package com.example.demo.controllers;

import com.example.demo.dto.ZendeskTicketRequest;
import com.example.demo.dto.ZendeskTicketResponse;
import com.example.demo.dto.ZendeskUserRequest;
import com.example.demo.dto.ZendeskUserResponse;
import com.example.demo.services.ZendeskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Zendesk Controller
 *
 * REST API endpoints for Zendesk customer support integration.
 *
 * All endpoints are public (no authentication required) for demo purposes.
 * In production, consider adding authentication and authorization.
 */
@RestController
@RequestMapping("/api/zendesk")
@Tag(name = "Zendesk Integration", description = "Customer support ticket and user management via Zendesk API")
public class ZendeskController {

    private final ZendeskService zendeskService;

    public ZendeskController(ZendeskService zendeskService) {
        this.zendeskService = zendeskService;
    }

    // Ticket Operations

    @PostMapping("/tickets")
    @Operation(summary = "Create a new support ticket",
               description = "Creates a new ticket in Zendesk. If the requester doesn't exist, they will be created automatically.")
    public ResponseEntity<ZendeskTicketResponse> createTicket(@RequestBody ZendeskTicketRequest request) {
        try {
            ZendeskTicketResponse response = zendeskService.createTicket(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/tickets/{id}")
    @Operation(summary = "Get ticket by ID",
               description = "Retrieves a specific ticket by its Zendesk ID")
    public ResponseEntity<ZendeskTicketResponse> getTicket(@PathVariable Long id) {
        ZendeskTicketResponse response = zendeskService.getTicket(id);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/tickets")
    @Operation(summary = "Get all tickets",
               description = "Retrieves all tickets from Zendesk")
    public ResponseEntity<List<ZendeskTicketResponse>> getAllTickets() {
        List<ZendeskTicketResponse> response = zendeskService.getAllTickets();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tickets/search")
    @Operation(summary = "Search tickets",
               description = "Search tickets using Zendesk query syntax. Examples: 'status:open', 'requester:john@example.com'")
    public ResponseEntity<List<ZendeskTicketResponse>> searchTickets(@RequestParam String q) {
        List<ZendeskTicketResponse> response = zendeskService.searchTickets(q);
        return ResponseEntity.ok(response);
    }

    // User Operations

    @PostMapping("/users")
    @Operation(summary = "Create a new user",
               description = "Creates a new user in Zendesk")
    public ResponseEntity<ZendeskUserResponse> createUser(@RequestBody ZendeskUserRequest request) {
        try {
            ZendeskUserResponse response = zendeskService.createUser(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID",
               description = "Retrieves a specific user by their Zendesk ID")
    public ResponseEntity<ZendeskUserResponse> getUser(@PathVariable Long id) {
        ZendeskUserResponse response = zendeskService.getUser(id);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users",
               description = "Retrieves all users from Zendesk")
    public ResponseEntity<List<ZendeskUserResponse>> getAllUsers() {
        List<ZendeskUserResponse> response = zendeskService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/search")
    @Operation(summary = "Find user by email",
               description = "Finds a user by their email address")
    public ResponseEntity<ZendeskUserResponse> findUserByEmail(@RequestParam String email) {
        ZendeskUserResponse response = zendeskService.findUserByEmail(email);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }
}

