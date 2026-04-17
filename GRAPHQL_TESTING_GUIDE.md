# GraphQL Todo App - Testing Guide

This guide explains how to use the GraphQL API for the Todo application.

## Access Points

### GraphQL Endpoint
- **URL**: `http://localhost:6060/graphql`
- **Method**: POST
- **Content-Type**: application/json

### GraphiQL UI (Interactive Explorer)
- **URL**: `http://localhost:6060/graphiql`
- **Method**: GET
- **Browser**: Open in any web browser for an interactive GraphQL IDE

## Sample Queries and Mutations

### 1. Get All Todos

```graphql
query {
  todos {
    id
    title
    description
    completed
  }
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:6060/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"query { todos { id title description completed } }"}'
```

### 2. Get a Specific Todo by ID

```graphql
query {
  todo(id: "REPLACE_WITH_TODO_ID") {
    id
    title
    description
    completed
  }
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:6060/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"query { todo(id: \"REPLACE_WITH_TODO_ID\") { id title description completed } }"}'
```

### 3. Create a New Todo

```graphql
mutation {
  createTodo(input: {
    title: "Learn GraphQL"
    description: "Understand GraphQL basics and implementation"
  }) {
    id
    title
    description
    completed
  }
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:6060/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { createTodo(input: { title: \"Learn GraphQL\", description: \"Understand GraphQL basics\" }) { id title description completed } }"}'
```

### 4. Update an Existing Todo

```graphql
mutation {
  updateTodo(id: "REPLACE_WITH_TODO_ID", input: {
    title: "Updated Title"
    description: "Updated Description"
    completed: true
  }) {
    id
    title
    description
    completed
  }
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:6060/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { updateTodo(id: \"REPLACE_WITH_TODO_ID\", input: { title: \"Updated\", completed: true }) { id title completed } }"}'
```

### 5. Mark a Todo as Completed

```graphql
mutation {
  completeTodo(id: "REPLACE_WITH_TODO_ID") {
    id
    title
    completed
  }
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:6060/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { completeTodo(id: \"REPLACE_WITH_TODO_ID\") { id title completed } }"}'
```

### 6. Delete a Todo

```graphql
mutation {
  deleteTodo(id: "REPLACE_WITH_TODO_ID")
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:6060/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { deleteTodo(id: \"REPLACE_WITH_TODO_ID\") }"}'
```

## Using GraphiQL UI

The easiest way to test GraphQL queries is using the interactive GraphiQL UI:

1. Open `http://localhost:6060/graphiql` in your browser
2. Type your query/mutation in the left panel
3. Click the "Play" button (▶) or press `Ctrl+Enter`
4. View results in the right panel
5. Use the "Docs" tab on the right to explore the schema

## REST API (Still Available)

The traditional REST API for todos is still available at:
- `GET /api/todos` - Get all todos
- `GET /api/todos/{id}` - Get a specific todo
- `POST /api/todos` - Create a new todo
- `PUT /api/todos/{id}` - Update a todo
- `DELETE /api/todos/{id}` - Delete a todo

## GraphQL Schema

The GraphQL schema is defined in `/src/main/resources/graphql/schema.graphqls`

### Types

#### Todo
```graphql
type Todo {
  id: ID!                    # Unique identifier
  title: String!             # Todo title
  description: String        # Optional description
  completed: Boolean!        # Completion status
}
```

#### Query
- `todos: [Todo!]!` - Get all todos
- `todo(id: ID!): Todo` - Get a specific todo

#### Mutation
- `createTodo(input: CreateTodoInput!): Todo!` - Create a new todo
- `updateTodo(id: ID!, input: UpdateTodoInput!): Todo` - Update a todo
- `deleteTodo(id: ID!): Boolean!` - Delete a todo
- `completeTodo(id: ID!): Todo` - Mark a todo as completed

## Project Structure

### GraphQL Components
- **Schema**: `/src/main/resources/graphql/schema.graphqls`
- **Query Resolver**: `/src/main/java/com/example/demo/graphql/TodoQueryResolver.java`
- **Mutation Resolver**: `/src/main/java/com/example/demo/graphql/TodoMutationResolver.java`
- **DTOs**: `/src/main/java/com/example/demo/dto/CreateTodoInput.java`, `UpdateTodoInput.java`
- **Configuration**: `/src/main/java/com/example/demo/config/GraphQLConfig.java`

### Service Layer
- **Service**: `/src/main/java/com/example/demo/services/TodoService.java`
- **Repository**: `/src/main/java/com/example/demo/repo/TodoRepository.java`
- **Model**: `/src/main/java/com/example/demo/model/Todo.java`

## Prerequisites

- MongoDB running at `localhost:27017` (for persistence)
- Application running on port 6060

## Notes

- All GraphQL endpoints are public (no authentication required)
- Todos are stored in MongoDB collection `todos`
- The GraphQL API uses Spring GraphQL with Spring Boot 3.5.6
- Both GraphQL and REST APIs operate on the same data

## Error Handling

GraphQL errors are returned in the `errors` field of the response:

```json
{
  "errors": [
    {
      "message": "Error message here",
      "locations": [
        {"line": 1, "column": 1}
      ]
    }
  ],
  "data": null
}
```

