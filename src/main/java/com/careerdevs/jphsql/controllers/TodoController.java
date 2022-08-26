package com.careerdevs.jphsql.controllers;

import com.careerdevs.jphsql.models.TodoModel;
import com.careerdevs.jphsql.repositories.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final String JPH_API_URL = "https://jsonplaceholder.typicode.com/todos";

    @Autowired
    private TodoRepository todoRepository;

    //    Getting all todo directly from JPH API
    @GetMapping("/jph/all")
    public ResponseEntity<?> getAllTodosAPI (RestTemplate restTemplate) {

        try {

            TodoModel[] allTodos = restTemplate.getForObject(JPH_API_URL, TodoModel[].class);

            return ResponseEntity.ok(allTodos);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //    Get all todo stored in out local MySQL DB
    @GetMapping("/sql/all")
    public ResponseEntity<?> getAllTodosSQL () {

        try {

            ArrayList<TodoModel> allTodos = (ArrayList<TodoModel>) todoRepository.findAll();

            return ResponseEntity.ok(allTodos);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/all")
    public ResponseEntity<?> uploadAllTodosToSQL (RestTemplate restTemplate) {

        try {

            TodoModel[] allTodos = restTemplate.getForObject(JPH_API_URL, TodoModel[].class);

//            TODO: remove id from each todo

            assert allTodos != null;
            List<TodoModel> savedTodos = todoRepository.saveAll(Arrays.asList(allTodos));

            return ResponseEntity.ok(savedTodos);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadOneTodo (@RequestBody TodoModel newTodoData) {

        try {

            newTodoData.removeId();

//            TODO: Data validation on the new todo data (make sure fields are valid values)

            TodoModel savedTodo = todoRepository.save(newTodoData);

            return ResponseEntity.ok(savedTodo);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
