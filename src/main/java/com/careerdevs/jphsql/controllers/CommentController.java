package com.careerdevs.jphsql.controllers;

import com.careerdevs.jphsql.models.CommentModel;
import com.careerdevs.jphsql.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;

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

    @PostMapping("/all")
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

            CommentModel savedComment = commentRepository.save(newCommentData);

            return ResponseEntity.ok(savedComment);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/findBodyMax")
    public ResponseEntity<?> findBodyMax (RestTemplate restTemplate) {

        try {
            //retrieve data from JPH API and save to array of comments
            CommentModel[] allComments = restTemplate.getForObject(JPH_API_URL, CommentModel[].class);

            //check that allComments is present, otherwise an exception will be thrown
            assert allComments != null;

            int maxLen = 0;
            for (CommentModel comment: allComments) {
                if (comment.getBody().length() > maxLen) maxLen = comment.getBody().length();
            }

            //remove id from each comment

            return ResponseEntity.ok("Max Length Body: " + maxLen);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
