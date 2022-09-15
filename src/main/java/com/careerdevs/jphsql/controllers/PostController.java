package com.careerdevs.jphsql.controllers;

import com.careerdevs.jphsql.models.PostModel;
import com.careerdevs.jphsql.models.UserModel;
import com.careerdevs.jphsql.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:3500")
public class PostController {
    private final String JPH_API_URL = "https://jsonplaceholder.typicode.com/posts";
    @Autowired
    private PostRepository postRepository;

    //      Getting all post directly from JPH API
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

    //      Get all post stored in out local MySQL DB
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

    //      Copy JPH SQL data in to jphsql database
    @PostMapping("/sql/all")
    public ResponseEntity<?> uploadAllPostToSQL(RestTemplate restTemplate) {
        try {
            //retrieve data from JPH API and save to array of AlbumModels
            PostModel[] allPost = restTemplate.getForObject(JPH_API_URL, PostModel[].class);

            //check that allPost is present, otherwise an exception will be thrown
            assert allPost != null;

            //remove id from each album
            for (int i = 0; i < allPost.length; i++) {
                allPost[i].removeId();
            }

            //saves albums to database and updates each Post's id field to the saved database ID
            postRepository.saveAll(Arrays.asList(allPost));

            //respond with the data that was just saved to the database
            return ResponseEntity.ok(allPost);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      POST one post into database as a PostMode
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

    //      GET one post by ID (from SQL DB)
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
//            return ResponseEntity.status(404).body("Post Not Found With ID: " + id);
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      DELETE one post by ID (from SQL DB) must make sure a post with the given id already exist
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

    //      DELETE All posts (from SQL DB) - returns how many were just deleted
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

    //      PUT one post by ID (from SQL DB) - must make sure a post with the given id already exist
    @PutMapping("/sql/{id}")
    public ResponseEntity<?> updateOnePost(@PathVariable String id, @RequestBody PostModel updatePostData) {
        try {

            Optional<PostModel> foundPost = findPost(id);
            if (foundPost.isEmpty()) return ResponseEntity.status(404).body("Post not found - id:" + id);

            if (foundPost.get().getId() != updatePostData.getId()) return ResponseEntity.status(400).body("Post IDs did not match");

            postRepository.save(updatePostData);
            return ResponseEntity.ok(updatePostData);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public Optional<PostModel> findPost (String id) throws NumberFormatException {
        int postId = Integer.parseInt(id);
        return postRepository.findById(postId);
    }
}












