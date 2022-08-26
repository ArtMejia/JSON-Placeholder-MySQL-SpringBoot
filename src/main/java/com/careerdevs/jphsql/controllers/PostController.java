package com.careerdevs.jphsql.controllers;

import com.careerdevs.jphsql.models.PostModel;
import com.careerdevs.jphsql.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/posts")
public class PostController {

    private final String JPH_API_URL = "https://jsonplaceholder.typicode.com/posts";

    @Autowired
    private PostRepository postRepository;

    //    Getting all user directly from JPH API
    @GetMapping("/jph/all")
    public ResponseEntity<?> getAllPostsAPI (RestTemplate restTemplate) {

        try {

            PostModel[] allPosts = restTemplate.getForObject(JPH_API_URL, PostModel[].class);

            return ResponseEntity.ok(allPosts);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //    Get all post stored in out local MySQL DB
    @GetMapping("/sql/all")
    public ResponseEntity<?> getAllPostsSQL () {

        try {

            ArrayList<PostModel> allPosts = (ArrayList<PostModel>) postRepository.findAll();

            return ResponseEntity.ok(allPosts);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/all")
    public ResponseEntity<?> uploadAllPostsToSQL (RestTemplate restTemplate) {

        try {

            PostModel[] allPost = restTemplate.getForObject(JPH_API_URL, PostModel[].class);

//            TODO: remove id from each user

            assert allPost != null;
            List<PostModel> savedPosts = postRepository.saveAll(Arrays.asList(allPost));

            return ResponseEntity.ok(savedPosts);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadOnePost (@RequestBody PostModel newPostData) {

        try {

            newPostData.removeId();

//            TODO: Data validation on the new user data (make sure fields are valid values)

            PostModel savedUser = postRepository.save(newPostData);

            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
