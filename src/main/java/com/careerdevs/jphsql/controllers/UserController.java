package com.careerdevs.jphsql.controllers;

import com.careerdevs.jphsql.models.UserModel;
import com.careerdevs.jphsql.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final String JPH_API_URL = "https://jsonplaceholder.typicode.com/users";

    @Autowired
    private UserRepository userRepository;

//    Getting all user directly from JPH API
    @GetMapping("/jph/all")
    public ResponseEntity<?> getAllUsersAPI (RestTemplate restTemplate) {

        try {

            UserModel[] allUsers = restTemplate.getForObject(JPH_API_URL, UserModel[].class);

            return ResponseEntity.ok(allUsers);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

//    Get all user stored in out local MySQL DB
    @GetMapping("/sql/all")
    public ResponseEntity<?> getAllUsersSQL () {

        try {

            ArrayList<UserModel> allUsers = (ArrayList<UserModel>) userRepository.findAll();

            return ResponseEntity.ok(allUsers);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/all")
    public ResponseEntity<?> uploadAllUsersToSQL (RestTemplate restTemplate) {

        try {

            UserModel[] allUsers = restTemplate.getForObject(JPH_API_URL, UserModel[].class);

//            TODO: remove id from each user

            assert allUsers != null;
            List<UserModel> savedUsers = userRepository.saveAll(Arrays.asList(allUsers));

            return ResponseEntity.ok(savedUsers);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadOneUser (@RequestBody UserModel newUserData) {

        try {

            newUserData.removeId();

//            TODO: Data validation on the new user data (make sure fields are valid values)

            UserModel savedUser = userRepository.save(newUserData);

            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

//    TODO: Complete the work we have done for the User resource for one more JPH resource of your choice (post)

//    TODO: GET one user by ID (from SQL DB)
//    TODO: DELETE All users (from SQL DB) - returns how many were just deleted
//    TODO: DELETE one user by ID (from SQL DB) must make sure a user with the given id already exist
//    TODO: PUT one user by ID (from SQL DB) - must make sure a user with the given id already exist

//    BONUS: Add address and company to UserModel


    @GetMapping("/id/{id}")
    public ResponseEntity<?> getOneUserById (@PathVariable Integer id) {
        try {

            UserModel oneUser = userRepository.getReferenceById(id);

            return ResponseEntity.ok(oneUser);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @DeleteMapping("/sql/all")
    public ResponseEntity<?> deleteAllUsersSQL () {

        try {

            ArrayList<UserModel> allUsers = (ArrayList<UserModel>) userRepository.findAll();

            return ResponseEntity.ok(allUsers);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateOneUser (@RequestBody UserModel updatedUserData) {

        try {

//            TODO: Data validation on the new user data (make sure fields are valid values)

            UserModel savedUser = userRepository.save(updatedUserData);

            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
