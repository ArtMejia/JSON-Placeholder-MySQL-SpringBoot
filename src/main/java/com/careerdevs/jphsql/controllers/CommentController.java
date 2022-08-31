package com.careerdevs.jphsql.controllers;

import com.careerdevs.jphsql.models.CommentModel;
import com.careerdevs.jphsql.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final String JPH_API_URL = "https://jsonplaceholder.typicode.com/comments";
    @Autowired
    private CommentRepository commentRepository;

    //    Getting all comment directly from JPH API
    @GetMapping("/jph/all")
    public ResponseEntity<?> getAllCommentsAPI (RestTemplate restTemplate) {
        try {
            CommentModel[] allComments = restTemplate.getForObject(JPH_API_URL, CommentModel[].class);
            return ResponseEntity.ok(allComments);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //    Get all comment stored in out local MySQL DB
    @GetMapping("/sql/all")
    public ResponseEntity<?> getAllCommentsSQL () {
        try {
            ArrayList<CommentModel> allComments = (ArrayList<CommentModel>) commentRepository.findAll();
            return ResponseEntity.ok(allComments);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/sql/all")
    public ResponseEntity<?> uploadAllCommentsToSQL (RestTemplate restTemplate) {
        try {
            //retrieve data from JPH API and save to array of comments
            CommentModel[] allComments = restTemplate.getForObject(JPH_API_URL, CommentModel[].class);

            //check that allComments is present, otherwise an exception will be thrown
            assert allComments != null;

            //remove id from each comment
            for (int i = 0; i < allComments.length; i++) {
                allComments[i].removeId();
            }
            //saves comment to database and updates each comment's id field to the saved database ID
            commentRepository.saveAll(Arrays.asList(allComments));

            //respond with the data that was just saved to the database
            return ResponseEntity.ok(allComments);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadOneComment (@RequestBody CommentModel newCommentData) {
        try {
            newCommentData.removeId();
//            TODO: Data validation on the new comment data (make sure fields are valid values)
            commentRepository.save(newCommentData);
            return ResponseEntity.ok(newCommentData);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //    GET one comment by ID (from SQL DB)
    @GetMapping("/sql/{id}")
    public ResponseEntity<?> getOneCommentByID (@PathVariable String id) {
        try {
            //throws NumberFormatException if id is not an int
            int commentId = Integer.parseInt(id);

            System.out.println("Getting Comment With ID: " + id);

            //GET DATA FROM SQL (using repo)
            Optional<CommentModel> foundComment = commentRepository.findById(commentId);

            if (foundComment.isEmpty()) return ResponseEntity.status(404).body("Comment Not Found With ID: " + id);
            //if (foundComment.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

            return ResponseEntity.ok(foundComment.get());

        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");
        }
        //TODO: reimplement exception throwing to handle 404 errors
//        catch (HttpClientErrorException e) {
//            return ResponseEntity.status(404).body("Comment Not Found With ID: " + id);
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //DELETE one comment by ID (from SQL DB) must make sure a comment with the given id already exist
    @DeleteMapping("/sql/{id}")
    public ResponseEntity<?> deleteOneCommentByID (@PathVariable String id) {
        try {
            //throws NumberFormatException if id is not an int
            int commentId = Integer.parseInt(id);
            System.out.println("Getting Comment With ID: " + id);

            //GET DATA FROM SQL (using repo)
            Optional<CommentModel> foundComment = commentRepository.findById(commentId);
            if (foundComment.isEmpty()) return ResponseEntity.status(404).body("Comment Not Found With ID: " + id);
            //if (foundComment.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

            commentRepository.deleteById(commentId);
            return ResponseEntity.ok(foundComment.get());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");
        }
        //TODO: reimplement exception throwing to handle 404 errors
//        catch (HttpClientErrorException e) {
//            return ResponseEntity.status(404).body("Comment Not Found With ID: " + id);
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //DELETE All comments (from SQL DB) - returns how many were just deleted
    @DeleteMapping("/sql/all")
    public ResponseEntity<?> deleteAllCommentsSQL() {
        try {
            long count = commentRepository.count();
            commentRepository.deleteAll();
            return ResponseEntity.ok("Deleted Comments: " + count);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      PUT one comment by ID (from SQL DB) - must make sure a comment with the given id already exist
    @PutMapping
    public ResponseEntity<?> updateOneComment(@RequestBody CommentModel newCommentData) {
        try {
            //TODO: Data validation on the new comment data (make sure fields are valid values)
            commentRepository.save(newCommentData);
            return ResponseEntity.ok(newCommentData);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

//      Used to get max length of body in comments
//    @GetMapping("/findBodyMax")
//    public ResponseEntity<?> findBodyMax (RestTemplate restTemplate) {
//
//        try {
//            //retrieve data from JPH API and save to array of comments
//            CommentModel[] allComments = restTemplate.getForObject(JPH_API_URL, CommentModel[].class);
//
//            //check that allComments is present, otherwise an exception will be thrown
//            assert allComments != null;
//
//            int maxLen = 0;
//            for (CommentModel comment: allComments) {
//                if (comment.getBody().length() > maxLen) maxLen = comment.getBody().length();
//            }
//
//            //remove id from each comment
//
//            return ResponseEntity.ok("Max Length Body: " + maxLen);
//
//        } catch (Exception e) {
//            System.out.println(e.getClass());
//            System.out.println(e.getMessage());
//            return ResponseEntity.internalServerError().body(e.getMessage());
//        }
//    }
}
