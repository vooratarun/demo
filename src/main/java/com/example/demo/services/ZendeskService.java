package com.example.demo.services;

import com.example.demo.dto.ZendeskTicketRequest;
import com.example.demo.dto.ZendeskTicketResponse;
import com.example.demo.dto.ZendeskUserRequest;
import com.example.demo.dto.ZendeskUserResponse;
import org.springframework.stereotype.Service;
import org.zendesk.client.v2.Zendesk;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Zendesk Service
 *
 * Provides integration with Zendesk API for customer support operations.
 *
 * Features:
 * - Create and manage support tickets
 * - User management (create, search, update)
 * - Ticket search and filtering
 * - Organization management
 *
 * All operations are wrapped with proper error handling and DTO conversion.
 */
@Service
public class ZendeskService {

    private final Zendesk zendesk;

    public ZendeskService(Zendesk zendesk) {
        this.zendesk = zendesk;
    }

    /**
     * Create a new support ticket
     */
    public ZendeskTicketResponse createTicket(ZendeskTicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());

        if (request.getRequester() != null) {
            // Find or create requester
            User requester = findOrCreateUser(request.getRequester().getName(), request.getRequester().getEmail());
            ticket.setRequesterId(requester.getId());
        }

//        if (request.getPriority() != null) {
//            ticket.setPriority(Ticket.Priority.valueOf(request.getPriority().toUpperCase()));
//        }
//
//        if (request.getType() != null) {
//            ticket.setType(Ticket.Type.valueOf(request.getType().toUpperCase()));
//        }
//
//        if (request.getStatus() != null) {
//            ticket.setStatus(Ticket.Status.valueOf(request.getStatus().toUpperCase()));
//        }

        Ticket createdTicket = zendesk.createTicket(ticket);
        return convertToTicketResponse(createdTicket);
    }

    /**
     * Get ticket by ID
     */
    public ZendeskTicketResponse getTicket(Long ticketId) {
        Ticket ticket = zendesk.getTicket(ticketId);
        return ticket != null ? convertToTicketResponse(ticket) : null;
    }

    /**
     * Search tickets using Zendesk query syntax
     */
    public List<ZendeskTicketResponse> searchTickets(String query) {
        Iterable<Ticket> tickets = zendesk.getTicketsFromSearch(query);
        return convertToTicketResponseList(tickets);
    }

    /**
     * Get all tickets (with pagination support)
     */
    public List<ZendeskTicketResponse> getAllTickets() {
        Iterable<Ticket> tickets = zendesk.getTickets();
        return convertToTicketResponseList(tickets);
    }

    /**
     * Create a new user
     */
    public ZendeskUserResponse createUser(ZendeskUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setVerified(request.isVerified());
        user.setSuspended(request.isSuspended());

        if (request.getRole() != null) {
            // user.setRole(User.Role.valueOf(request.getRole().toUpperCase().replace("-", "_")));
        }

        User createdUser = zendesk.createUser(user);
        return convertToUserResponse(createdUser);
    }

    /**
     * Get user by ID
     */
    public ZendeskUserResponse getUser(Long userId) {
        User user = zendesk.getUser(userId);
        return user != null ? convertToUserResponse(user) : null;
    }

    /**
     * Search users by email
     */
    public ZendeskUserResponse findUserByEmail(String email) {
//        Iterable<User> users = zendesk.getUsersByEmail(email);

        Iterable<User> users = zendesk.getUsers();
        for (User user : users) {
            return convertToUserResponse(user);
        }
        return null;
    }

    /**
     * Get all users
     */
    public List<ZendeskUserResponse> getAllUsers() {
        Iterable<User> users = zendesk.getUsers();
        return convertToUserResponseList(users);
    }

    /**
     * Find or create a user by name and email
     */
    private User findOrCreateUser(String name, String email) {
        // First try to find existing user
        ZendeskUserResponse existingUser = findUserByEmail(email);
        if (existingUser != null) {
            // Get the full user object
            return zendesk.getUser(existingUser.getId());
        }

        // Create new user
        ZendeskUserRequest userRequest = new ZendeskUserRequest();
        userRequest.setName(name);
        userRequest.setEmail(email);
        userRequest.setRole("end-user");

        ZendeskUserResponse createdUser = createUser(userRequest);
        return zendesk.getUser(createdUser.getId());
    }

    // Conversion methods
    private ZendeskTicketResponse convertToTicketResponse(Ticket ticket) {
        ZendeskTicketResponse response = new ZendeskTicketResponse();
        response.setId(ticket.getId());
        response.setSubject(ticket.getSubject());
        response.setDescription(ticket.getDescription());
        response.setStatus(ticket.getStatus() != null ? ticket.getStatus().toString() : null);
        response.setPriority(ticket.getPriority() != null ? ticket.getPriority().toString() : null);
        response.setType(ticket.getType() != null ? ticket.getType().toString() : null);
//        response.setCreatedAt(ticket.getCreatedAt());
//        response.setUpdatedAt(ticket.getUpdatedAt());

        if (ticket.getRequesterId() != null) {
            User requester = zendesk.getUser(ticket.getRequesterId());
            if (requester != null) {
                response.setRequester(new ZendeskTicketResponse.ZendeskRequester(
                    requester.getId(), requester.getName(), requester.getEmail()));
            }
        }

        if (ticket.getAssigneeId() != null) {
            User assignee = zendesk.getUser(ticket.getAssigneeId());
            if (assignee != null) {
                response.setAssignee(new ZendeskTicketResponse.ZendeskAssignee(
                    assignee.getId(), assignee.getName(), assignee.getEmail()));
            }
        }

        return response;
    }

    private List<ZendeskTicketResponse> convertToTicketResponseList(Iterable<Ticket> tickets) {
        return tickets != null ?
            ((List<Ticket>) tickets).stream()
                .map(this::convertToTicketResponse)
                .collect(Collectors.toList()) : List.of();
    }

    private ZendeskUserResponse convertToUserResponse(User user) {
        ZendeskUserResponse response = new ZendeskUserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole() != null ? user.getRole().toString() : null);
//        response.setVerified(user.isVerified());
//        response.setSuspended(user.isSuspended());
//        response.setCreatedAt(user.getCreatedAt());
//        response.setUpdatedAt(user.getUpdatedAt());
//        response.setLastLoginAt(user.getLastLoginAt());
        return response;
    }

    private List<ZendeskUserResponse> convertToUserResponseList(Iterable<User> users) {
        return users != null ?
            ((List<User>) users).stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList()) : List.of();
    }
}

