package com.careerdevs.jphsql.controllers;

import com.careerdevs.jphsql.models.TodoModel;
import com.careerdevs.jphsql.repositories.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final String JPH_API_URL = "https://jsonplaceholder.typicode.com/todos";
    @Autowired
    private TodoRepository todoRepository;

    //    Getting all todos directly from JPH API
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

    //    Get all todos stored in out local MySQL DB
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

    //      Copy JPH SQL data in to jphsql database
    @PostMapping("/sql/all")
    public ResponseEntity<?> uploadAllTodoToSQL(RestTemplate restTemplate) {
        try {
            //retrieve data from JPH API and save to array of TodoModels
            TodoModel[] allTodo = restTemplate.getForObject(JPH_API_URL, TodoModel[].class);

            //check that allTodo is present, otherwise an exception will be thrown
            assert allTodo != null;

            //remove id from each todos
            for (int i = 0; i < allTodo.length; i++) {
                allTodo[i].removeId();
            }

            //saves todos to database and updates each Todos id field to the saved database ID
            todoRepository.saveAll(Arrays.asList(allTodo));

            //respond with the data that was just saved to the database
            return ResponseEntity.ok(allTodo);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      POST one todos into database as a TodoModel
    @PostMapping
    public ResponseEntity<?> uploadOneTodo (@RequestBody TodoModel newTodoData) {
        try {
            newTodoData.removeId();
//            TODO: Data validation on the new todo data (make sure fields are valid values)
            todoRepository.save(newTodoData);
            return ResponseEntity.ok(newTodoData);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      GET one todos by ID (from SQL DB)
    @GetMapping("/sql/{id}")
    public ResponseEntity<?> getOneTodoByID (@PathVariable String id) {
        try {
            //throws NumberFormatException if id is not an int
            int todoId = Integer.parseInt(id);
            System.out.println("Getting Todo With ID: " + id);

            //GET DATA FROM SQL (using repo)
            Optional<TodoModel> foundTodo = todoRepository.findById(todoId);

            if (foundTodo.isEmpty()) return ResponseEntity.status(404).body("Todo Not Found With ID: " + id);
            //if (foundTodo.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

            return ResponseEntity.ok(foundTodo.get());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");
        }
        //TODO: reimplement exception throwing to handle 404 errors
//        catch (HttpClientErrorException e) {
//            return ResponseEntity.status(404).body("Todos Not Found With ID: " + id);
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      DELETE one todo by ID (from SQL DB) must make sure a todo with the given id already exist
    @DeleteMapping("/sql/{id}")
    public ResponseEntity<?> deleteOneTodoByID (@PathVariable String id) {
        try {
            //throws NumberFormatException if id is not an int
            int todoId = Integer.parseInt(id);
            System.out.println("Getting Todo With ID: " + id);

            //GET DATA FROM SQL (using repo)
            Optional<TodoModel> foundTodo = todoRepository.findById(todoId);
            if (foundTodo.isEmpty()) return ResponseEntity.status(404).body("Todo Not Found With ID: " + id);
            //if (foundTodo.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

            todoRepository.deleteById(todoId);
            return ResponseEntity.ok(foundTodo.get());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");
        }
        //TODO: reimplement exception throwing to handle 404 errors
//        catch (HttpClientErrorException e) {
//            return ResponseEntity.status(404).body("Todo Not Found With ID: " + id);
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      DELETE All todos (from SQL DB) - returns how many were just deleted
    @DeleteMapping("/sql/all")
    public ResponseEntity<?> deleteAllTodosSQL() {
        try {
            long count = todoRepository.count();
            todoRepository.deleteAll();
            return ResponseEntity.ok("Deleted Todos: " + count);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      PUT one todos by ID (from SQL DB) - must make sure an todo with the given id already exist
    @PutMapping
    public ResponseEntity<?> updateOneTodo(@RequestBody TodoModel newTodoData) {
        try {
            //TODO: Data validation on the new todo (make sure fields are valid values)
            todoRepository.save(newTodoData);
            return ResponseEntity.ok(newTodoData);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
