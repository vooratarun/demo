package com.example.demo.services;

import com.example.demo.model.DynamoTodo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DynamoTodoService {

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbTable<DynamoTodo> todoTable;
    private final String tableName;

    public DynamoTodoService(
            DynamoDbClient dynamoDbClient,
            DynamoDbTable<DynamoTodo> todoTable,
            @Value("${aws.dynamodb.table-name:todos}") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.todoTable = todoTable;
        this.tableName = tableName;
    }

    public DynamoTodo create(DynamoTodo todo) {
        ensureTableExists();
        if (todo.getId() == null || todo.getId().isBlank()) {
            todo.setId(UUID.randomUUID().toString());
        }
        if (todo.getCompleted() == null) {
            todo.setCompleted(false);
        }
        todoTable.putItem(todo);
        return todo;
    }

    public List<DynamoTodo> getAll() {
        ensureTableExists();
        return todoTable.scan().items().stream().toList();
    }

    public Optional<DynamoTodo> getById(String id) {
        ensureTableExists();
        return Optional.ofNullable(todoTable.getItem(Key.builder().partitionValue(id).build()));
    }

    public Optional<DynamoTodo> update(String id, DynamoTodo updatedTodo) {
        ensureTableExists();
        DynamoTodo existing = todoTable.getItem(Key.builder().partitionValue(id).build());
        if (existing == null) {
            return Optional.empty();
        }
        existing.setTitle(updatedTodo.getTitle());
        existing.setDescription(updatedTodo.getDescription());
        existing.setCompleted(updatedTodo.getCompleted() != null ? updatedTodo.getCompleted() : Boolean.FALSE);
        todoTable.putItem(existing);
        return Optional.of(existing);
    }

    public boolean delete(String id) {
        ensureTableExists();
        Key key = Key.builder().partitionValue(id).build();
        DynamoTodo existing = todoTable.getItem(key);
        if (existing == null) {
            return false;
        }
        todoTable.deleteItem(key);
        return true;
    }

    private void ensureTableExists() {
        try {
            dynamoDbClient.describeTable(builder -> builder.tableName(tableName));
        } catch (ResourceNotFoundException ex) {
            dynamoDbClient.createTable(
                    CreateTableRequest.builder()
                            .tableName(tableName)
                            .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                            .attributeDefinitions(AttributeDefinition.builder()
                                    .attributeName("id")
                                    .attributeType(ScalarAttributeType.S)
                                    .build())
                            .billingMode(BillingMode.PAY_PER_REQUEST)
                            .build()
            );
        }
    }
}
