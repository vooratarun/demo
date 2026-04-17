package com.example.demo.graphql;

import com.example.demo.model.Todo;
import com.example.demo.services.TodoService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TodoQueryResolver {
    private final TodoService todoService;

    public TodoQueryResolver(TodoService todoService) {
        this.todoService = todoService;
    }

    @QueryMapping
    public List<Todo> todos() {
        return todoService.getAllTodos();
    }

    @QueryMapping
    public Todo todo(@Argument String id) {
        return todoService.getTodoById(id).orElse(null);
    }
}

