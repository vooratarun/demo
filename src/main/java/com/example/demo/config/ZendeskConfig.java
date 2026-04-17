package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zendesk.client.v2.Zendesk;

/**
 * Zendesk Configuration
 *
 * Configures Zendesk API client for customer support integration.
 *
 * Features:
 * - Ticket creation and management
 * - User management
 * - Organization management
 * - Search functionality
 *
 * API Endpoints:
 * - POST /api/zendesk/tickets - Create ticket
 * - GET /api/zendesk/tickets/{id} - Get ticket by ID
 * - GET /api/zendesk/tickets/search?q={query} - Search tickets
 * - GET /api/zendesk/users - List users
 * - POST /api/zendesk/users - Create user
 *
 * Configuration Properties:
 * - zendesk.subdomain: Your Zendesk subdomain
 * - zendesk.username: Zendesk admin email
 * - zendesk.token: Zendesk API token
 * - zendesk.base-url: Full Zendesk API base URL
 * - zendesk.connect-timeout: Connection timeout in ms
 * - zendesk.read-timeout: Read timeout in ms
 *
 * Sample Usage:
 *
 * 1. Create a ticket:
 *    POST /api/zendesk/tickets
 *    {
 *      "subject": "Support Request",
 *      "description": "I need help with...",
 *      "requester": {
 *        "name": "John Doe",
 *        "email": "john@example.com"
 *      }
 *    }
 *
 * 2. Search tickets:
 *    GET /api/zendesk/tickets/search?q=status:open
 *
 * 3. Get ticket details:
 *    GET /api/zendesk/tickets/12345
 */
@Configuration
@ConfigurationProperties(prefix = "zendesk")
public class ZendeskConfig {

    private String subdomain;
    private String username;
    private String token;
    private String baseUrl;
    private int connectTimeout = 10000;
    private int readTimeout = 30000;

    @Bean
    public Zendesk zendeskClient() {
        return new Zendesk.Builder(baseUrl)
                .setUsername(username)
                .setToken(token)
                //.setConnectTimeout(connectTimeout)
                //.setReadTimeout(readTimeout)
                .build();
    }

    // Getters and setters
    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}

