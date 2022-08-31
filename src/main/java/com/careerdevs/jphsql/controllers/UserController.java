package com.careerdevs.jphsql.controllers;

import com.careerdevs.jphsql.models.UserModel;
import com.careerdevs.jphsql.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3500")
public class UserController {
    private final String JPH_API_URL = "https://jsonplaceholder.typicode.com/users";
    @Autowired
    private UserRepository userRepository;

    //      Getting all user directly from JPH API
    @GetMapping("/jph/all")
    public ResponseEntity<?> getAllUsersAPI(RestTemplate restTemplate) {
        try {
            UserModel[] allUsers = restTemplate.getForObject(JPH_API_URL, UserModel[].class);
            return ResponseEntity.ok(allUsers);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      Get all user stored in out local MySQL DB
    @GetMapping("/sql/all")
    public ResponseEntity<?> getAllUsersSQL() {
        try {
            ArrayList<UserModel> allUsers = (ArrayList<UserModel>) userRepository.findAll();
            return ResponseEntity.ok(allUsers);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      Copy JPH SQL data in to jphsql database
    @PostMapping("/sql/all")
    public ResponseEntity<?> uploadAllUsersToSQL(RestTemplate restTemplate) {
        try {
            //retrieve data from JPH API and save to array of UserModels
            UserModel[] allUsers = restTemplate.getForObject(JPH_API_URL, UserModel[].class);

            //check that allUsers is present, otherwise an exception will be thrown
            assert allUsers != null;

            //remove id from each user
            for (UserModel allUser : allUsers) {
                allUser.removeId();
            }

            //saves users to database and updates each User's id field to the saved database ID
            userRepository.saveAll(Arrays.asList(allUsers));

            //respond with the data that was just saved to the database
            return ResponseEntity.ok(allUsers);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      POST one user into database as a UserModel
    @PostMapping
    public ResponseEntity<?> uploadOneUser(@RequestBody UserModel newUserData) {
        try {
            newUserData.removeId();
            //TODO: Data validation on the new user data (make sure fields are valid values)
            userRepository.save(newUserData);
            return ResponseEntity.ok(newUserData);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      GET one user by ID (from SQL DB)
    @GetMapping("/sql/{id}")
    public ResponseEntity<?> getOneUserByID (@PathVariable String id) {
        try {
            //throws NumberFormatException if id is not an int
//            int userId = Integer.parseInt(id);
//            System.out.println("Getting User With ID: " + id);
//
//            //GET DATA FROM SQL (using repo)
//            Optional<UserModel> foundUser = userRepository.findById(userId);

            Optional<UserModel> foundUser = findUser(id);
            if (foundUser.isEmpty()) return ResponseEntity.status(404).body("User Not Found With ID: " + id);
            //if (foundUser.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

            return ResponseEntity.ok(foundUser.get());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");
        }
        //TODO: reimplement exception throwing to handle 404 errors
//        catch (HttpClientErrorException e) {
//            return ResponseEntity.status(404).body("User Not Found With ID: " + id);
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      DELETE one user by ID (from SQL DB) must make sure a user with the given id already exist
    @DeleteMapping("/sql/{id}")
    public ResponseEntity<?> deleteOneUserByID (@PathVariable String id) {
        try {
            //  throws NumberFormatException if id is not an int
            int userId = Integer.parseInt(id);
            System.out.println("Getting User With ID: " + id);

            //  GET DATA FROM SQL (using repo)
            Optional<UserModel> foundUser = userRepository.findById(userId);
            if (foundUser.isEmpty()) return ResponseEntity.status(404).body("User Not Found With ID: " + id);
            //if (foundUser.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

            userRepository.deleteById(userId);
            return ResponseEntity.ok(foundUser.get());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");
        }
        //TODO: reimplement exception throwing to handle 404 errors
//        catch (HttpClientErrorException e) {
//            return ResponseEntity.status(404).body("User Not Found With ID: " + id);
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      DELETE All users (from SQL DB) - returns how many were just deleted
    @DeleteMapping("/sql/all")
    public ResponseEntity<?> deleteAllUsersSQL() {
        try {
            long count = userRepository.count();
            userRepository.deleteAll();
            return ResponseEntity.ok("Deleted Users: " + count);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      PUT one user by ID (from SQL DB) - must make sure a user with the given id already exist
    @PutMapping("/sql/{id}")
    public ResponseEntity<?> updateOneUser(@PathVariable String id, @RequestBody UserModel updateUserData) {
        try {

            Optional<UserModel> foundUser = findUser(id);
            if (foundUser.isEmpty()) return ResponseEntity.status(404).body("User not found - id:" + id);

            if (foundUser.get().getId() != updateUserData.getId()) return ResponseEntity.status(400).body("User IDs did not match");

            userRepository.save(updateUserData);
            return ResponseEntity.ok(updateUserData);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public Optional<UserModel> findUser (String id) throws NumberFormatException {
        int userId = Integer.parseInt(id);
        return userRepository.findById(userId);
    }
}