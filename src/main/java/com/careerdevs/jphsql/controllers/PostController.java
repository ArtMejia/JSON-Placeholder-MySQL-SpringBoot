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
import java.util.Optional;

@RestController
@RequestMapping("api/posts")
public class PostController {

    private final String JPH_API_URL = "https://jsonplaceholder.typicode.com/posts";

    @Autowired
    private PostRepository postRepository;

    //    Getting all post directly from JPH API
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

//            TODO: remove id from each post

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

//            TODO: Data validation on the new post data (make sure fields are valid values)

            postRepository.save(newPostData);

            return ResponseEntity.ok(newPostData);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //    GET one post by ID (from SQL DB)
    @GetMapping("/sql/{id}")
    public ResponseEntity<?> getOnePostByID (@PathVariable String id) {
        try {

            //throws NumberFormatException if id is not an int
            int postId = Integer.parseInt(id);

            System.out.println("Getting Post With ID: " + id);

            //GET DATA FROM SQL (using repo)
            Optional<PostModel> foundPost = postRepository.findById(postId);

            if (foundPost.isEmpty()) return ResponseEntity.status(404).body("Post Not Found With ID: " + id);
            //if (foundPost.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

            return ResponseEntity.ok(foundPost.get());

        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");
        }
        //TODO: reimplement exception throwing to handle 404 errors
//        catch (HttpClientErrorException e) {
//
//            return ResponseEntity.status(404).body("Post Not Found With ID: " + id);
//
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //DELETE one post by ID (from SQL DB) must make sure a post with the given id already exist
    @DeleteMapping("/sql/{id}")
    public ResponseEntity<?> deleteOnePostByID (@PathVariable String id) {
        try {

            //throws NumberFormatException if id is not an int
            int postId = Integer.parseInt(id);

            System.out.println("Getting Post With ID: " + id);

            //GET DATA FROM SQL (using repo)
            Optional<PostModel> foundPost = postRepository.findById(postId);

            if (foundPost.isEmpty()) return ResponseEntity.status(404).body("Post Not Found With ID: " + id);
            //if (foundPost.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

            postRepository.deleteById(postId);

            return ResponseEntity.ok(foundPost.get());

        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");
        }
        //TODO: reimplement exception throwing to handle 404 errors
//        catch (HttpClientErrorException e) {
//            return ResponseEntity.status(404).body("Post Not Found With ID: " + id);
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //DELETE All posts (from SQL DB) - returns how many were just deleted
    @DeleteMapping("/sql/all")
    public ResponseEntity<?> deleteAllPostsSQL() {

        try {

            long count = postRepository.count();
            postRepository.deleteAll();
            return ResponseEntity.ok("Deleted Posts: " + count);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
