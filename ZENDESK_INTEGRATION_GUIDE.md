# Zendesk Integration - Testing Guide

This guide explains how to use the Zendesk customer support integration in the Spring Boot application.

## Overview

The Zendesk integration provides REST API endpoints for:
- Creating and managing support tickets
- User management (create, search, retrieve)
- Ticket search and filtering
- Organization management capabilities

## Configuration

### Application Properties
```properties
# Zendesk Configuration
zendesk.subdomain=demo-subdomain
zendesk.username=demo@example.com
zendesk.token=demo-api-token
zendesk.base-url=https://demo-subdomain.zendesk.com/api/v2
zendesk.connect-timeout=10000
zendesk.read-timeout=30000
```

### Setup Requirements
1. **Zendesk Account**: Active Zendesk subscription
2. **API Token**: Generated from Zendesk admin panel
3. **Subdomain**: Your Zendesk subdomain
4. **Admin Email**: Zendesk admin email address

## API Endpoints

### Base URL
```
http://localhost:6060/api/zendesk
```

### Ticket Operations

#### 1. Create a Ticket
**Endpoint**: `POST /api/zendesk/tickets`

**Request Body**:
```json
{
  "subject": "Login Issue",
  "description": "I cannot log into my account. Please help!",
  "requester": {
    "name": "John Doe",
    "email": "john.doe@example.com"
  },
  "priority": "normal",
  "type": "incident",
  "status": "new"
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:6060/api/zendesk/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "Login Issue",
    "description": "I cannot log into my account",
    "requester": {
      "name": "John Doe",
      "email": "john.doe@example.com"
    },
    "priority": "normal",
    "type": "incident"
  }'
```

**Response**:
```json
{
  "id": 12345,
  "subject": "Login Issue",
  "description": "I cannot log into my account",
  "status": "new",
  "priority": "normal",
  "type": "incident",
  "requester": {
    "id": 67890,
    "name": "John Doe",
    "email": "john.doe@example.com"
  },
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

#### 2. Get Ticket by ID
**Endpoint**: `GET /api/zendesk/tickets/{id}`

**cURL Example**:
```bash
curl http://localhost:6060/api/zendesk/tickets/12345
```

#### 3. Get All Tickets
**Endpoint**: `GET /api/zendesk/tickets`

**cURL Example**:
```bash
curl http://localhost:6060/api/zendesk/tickets
```

#### 4. Search Tickets
**Endpoint**: `GET /api/zendesk/tickets/search?q={query}`

**Search Examples**:
- `status:open` - Find all open tickets
- `requester:john@example.com` - Find tickets by requester email
- `priority:high` - Find high priority tickets
- `type:incident` - Find incident tickets

**cURL Examples**:
```bash
# Search for open tickets
curl "http://localhost:6060/api/zendesk/tickets/search?q=status:open"

# Search by requester email
curl "http://localhost:6060/api/zendesk/tickets/search?q=requester:john@example.com"

# Search for high priority tickets
curl "http://localhost:6060/api/zendesk/tickets/search?q=priority:high"
```

### User Operations

#### 1. Create a User
**Endpoint**: `POST /api/zendesk/users`

**Request Body**:
```json
{
  "name": "Jane Smith",
  "email": "jane.smith@example.com",
  "phone": "+1-555-0123",
  "role": "end-user",
  "verified": false,
  "suspended": false
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:6060/api/zendesk/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "phone": "+1-555-0123",
    "role": "end-user"
  }'
```

#### 2. Get User by ID
**Endpoint**: `GET /api/zendesk/users/{id}`

**cURL Example**:
```bash
curl http://localhost:6060/api/zendesk/users/67890
```

#### 3. Get All Users
**Endpoint**: `GET /api/zendesk/users`

**cURL Example**:
```bash
curl http://localhost:6060/api/zendesk/users
```

#### 4. Find User by Email
**Endpoint**: `GET /api/zendesk/users/search?email={email}`

**cURL Example**:
```bash
curl "http://localhost:6060/api/zendesk/users/search?email=jane.smith@example.com"
```

## Field Definitions

### Ticket Fields
- **subject**: Ticket title (required)
- **description**: Detailed description (required)
- **requester**: Customer information (auto-created if doesn't exist)
- **priority**: `low`, `normal`, `high`, `urgent`
- **type**: `question`, `incident`, `problem`, `task`
- **status**: `new`, `open`, `pending`, `hold`, `solved`, `closed`

### User Fields
- **name**: Full name (required)
- **email**: Email address (required, unique)
- **phone**: Phone number (optional)
- **role**: `end-user`, `agent`, `admin`
- **verified**: Email verification status
- **suspended**: Account suspension status

## Error Handling

The API returns standard HTTP status codes:
- `200`: Success
- `400`: Bad Request (invalid data)
- `404`: Not Found (ticket/user doesn't exist)
- `500`: Internal Server Error

Error responses include details about what went wrong.

## Integration Examples

### Creating a Support Ticket from Your App
```java
@RestController
public class SupportController {
    private final ZendeskService zendeskService;

    @PostMapping("/support/ticket")
    public ZendeskTicketResponse createSupportTicket(@RequestBody SupportRequest request) {
        ZendeskTicketRequest ticketRequest = new ZendeskTicketRequest();
        ticketRequest.setSubject(request.getSubject());
        ticketRequest.setDescription(request.getDescription());
        ticketRequest.setRequester(new ZendeskTicketRequest.ZendeskRequester(
            request.getCustomerName(),
            request.getCustomerEmail()
        ));
        ticketRequest.setPriority("normal");
        ticketRequest.setType("question");

        return zendeskService.createTicket(ticketRequest);
    }
}
```

### Searching for Customer Tickets
```java
@GetMapping("/support/tickets/{email}")
public List<ZendeskTicketResponse> getCustomerTickets(@PathVariable String email) {
    return zendeskService.searchTickets("requester:" + email);
}
```

## Testing with Mock Data

For development/testing without a real Zendesk instance, you can:

1. Use Zendesk's sandbox environment
2. Configure with test credentials
3. Mock the ZendeskService for unit tests

## Security Considerations

- Store API tokens securely (environment variables, secrets manager)
- Consider rate limiting for API calls
- Implement proper authentication for production use
- Validate input data to prevent injection attacks

## Monitoring

Monitor Zendesk API usage:
- Response times
- Success/failure rates
- Rate limit usage
- Ticket creation volume

## Project Structure

```
src/main/java/com/example/demo/
├── config/
│   └── ZendeskConfig.java              # Configuration and Zendesk client bean
├── controllers/
│   └── ZendeskController.java          # REST API endpoints
├── dto/
│   ├── ZendeskTicketRequest.java       # Ticket creation DTO
│   ├── ZendeskTicketResponse.java      # Ticket response DTO
│   ├── ZendeskUserRequest.java         # User creation DTO
│   └── ZendeskUserResponse.java        # User response DTO
└── services/
    └── ZendeskService.java             # Business logic and API integration
```

## Dependencies

- `zendesk-java-client`: Official Zendesk Java SDK
- Spring Boot Web: REST endpoints
- Spring Boot Configuration: Property binding

## Next Steps

1. Add webhook support for real-time ticket updates
2. Implement ticket comments and attachments
3. Add organization management
4. Create custom fields support
5. Add ticket escalation workflows
6. Implement SLA monitoring
7. Add reporting and analytics

