package com.example.demo.graphql;

import com.example.demo.dto.CreateTodoInput;
import com.example.demo.dto.UpdateTodoInput;
import com.example.demo.model.Todo;
import com.example.demo.services.TodoService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TodoMutationResolver {
    private final TodoService todoService;

    public TodoMutationResolver(TodoService todoService) {
        this.todoService = todoService;
    }

    @MutationMapping
    public Todo createTodo(@Argument CreateTodoInput input) {
        Todo todo = new Todo();
        todo.setTitle(input.getTitle());
        todo.setDescription(input.getDescription());
        todo.setCompleted(false);
        return todoService.createTodo(todo);
    }

    @MutationMapping
    public Todo updateTodo(@Argument String id, @Argument UpdateTodoInput input) {
        return todoService.getTodoById(id).map(todo -> {
            if (input.getTitle() != null) {
                todo.setTitle(input.getTitle());
            }
            if (input.getDescription() != null) {
                todo.setDescription(input.getDescription());
            }
            if (input.getCompleted() != null) {
                todo.setCompleted(input.getCompleted());
            }
            return todoService.createTodo(todo);
        }).orElse(null);
    }

    @MutationMapping
    public boolean deleteTodo(@Argument String id) {
        try {
            todoService.deleteTodo(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @MutationMapping
    public Todo completeTodo(@Argument String id) {
        return todoService.getTodoById(id).map(todo -> {
            todo.setCompleted(true);
            return todoService.createTodo(todo);
        }).orElse(null);
    }
}

