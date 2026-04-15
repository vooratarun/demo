package com.example.demo.controllers;

import com.example.demo.model.DynamoTodo;
import com.example.demo.services.DynamoTodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dynamodb/todos")
public class DynamoTodoController {

    private final DynamoTodoService todoService;

    public DynamoTodoController(DynamoTodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<DynamoTodo> createTodo(@RequestBody DynamoTodo todo) {
        return ResponseEntity.ok(todoService.create(todo));
    }

    @GetMapping
    public ResponseEntity<List<DynamoTodo>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DynamoTodo> getTodoById(@PathVariable String id) {
        return todoService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DynamoTodo> updateTodo(@PathVariable String id, @RequestBody DynamoTodo todo) {
        return todoService.update(id, todo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String id) {
        if (todoService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
