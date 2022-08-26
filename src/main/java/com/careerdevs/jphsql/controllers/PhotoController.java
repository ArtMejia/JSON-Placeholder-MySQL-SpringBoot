package com.careerdevs.jphsql.controllers;

import com.careerdevs.jphsql.models.PhotoModel;
import com.careerdevs.jphsql.repositories.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final String JPH_API_URL = "https://jsonplaceholder.typicode.com/photos";

    @Autowired
    private PhotoRepository photoRepository;

    //    Getting all photo directly from JPH API
    @GetMapping("/jph/all")
    public ResponseEntity<?> getAllPhotosAPI(RestTemplate restTemplate) {

        try {

            PhotoModel[] allPhotos = restTemplate.getForObject(JPH_API_URL, PhotoModel[].class);

            return ResponseEntity.ok(allPhotos);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //    Get all photo stored in out local MySQL DB
    @GetMapping("/sql/all")
    public ResponseEntity<?> getAllPhotosSQL() {

        try {

            ArrayList<PhotoModel> allPhotos = (ArrayList<PhotoModel>) photoRepository.findAll();

            return ResponseEntity.ok(allPhotos);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/all")
    public ResponseEntity<?> uploadAllPhotosToSQL(RestTemplate restTemplate) {

        try {

            PhotoModel[] allPhotos = restTemplate.getForObject(JPH_API_URL, PhotoModel[].class);

//            TODO: remove id from each photo

            assert allPhotos != null;
            List<PhotoModel> savedPhotos = photoRepository.saveAll(Arrays.asList(allPhotos));

            return ResponseEntity.ok(savedPhotos);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadOnePhoto(@RequestBody PhotoModel newPhotoData) {

        try {

            newPhotoData.removeId();

//            TODO: Data validation on the new photo data (make sure fields are valid values)

            PhotoModel savedPhoto = photoRepository.save(newPhotoData);

            return ResponseEntity.ok(savedPhoto);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
