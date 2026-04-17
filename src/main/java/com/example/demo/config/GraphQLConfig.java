package com.example.demo.config;

import org.springframework.context.annotation.Configuration;

/**
 * GraphQL Configuration
 *
 * Enables GraphQL endpoint for Todo operations.
 *
 * GraphQL Endpoint: POST /graphql
 * GraphQL UI (GraphiQL): GET /graphiql
 *
 * Sample Queries:
 *
 * 1. Get all todos:
 *    query {
 *      todos {
 *        id
 *        title
 *        description
 *        completed
 *      }
 *    }
 *
 * 2. Get a specific todo:
 *    query {
 *      todo(id: "todo-id-here") {
 *        id
 *        title
 *        description
 *        completed
 *      }
 *    }
 *
 * 3. Create a new todo:
 *    mutation {
 *      createTodo(input: {
 *        title: "My new todo"
 *        description: "This is a test todo"
 *      }) {
 *        id
 *        title
 *        description
 *        completed
 *      }
 *    }
 *
 * 4. Update a todo:
 *    mutation {
 *      updateTodo(id: "todo-id-here", input: {
 *        title: "Updated title"
 *        completed: true
 *      }) {
 *        id
 *        title
 *        completed
 *      }
 *    }
 *
 * 5. Delete a todo:
 *    mutation {
 *      deleteTodo(id: "todo-id-here")
 *    }
 *
 * 6. Mark a todo as completed:
 *    mutation {
 *      completeTodo(id: "todo-id-here") {
 *        id
 *        title
 *        completed
 *      }
 *    }
 */
@Configuration
public class GraphQLConfig {
}

