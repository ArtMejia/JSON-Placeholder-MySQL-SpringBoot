package com.careerdevs.jphsql.controllers;

import com.careerdevs.jphsql.models.PhotoModel;
import com.careerdevs.jphsql.repositories.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

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

    //      Copy JPH SQL data in to jphsql database
    @PostMapping("/sql/all")
    public ResponseEntity<?> uploadAllPhotoToSQL(RestTemplate restTemplate) {
        try {
            //retrieve data from JPH API and save to array of PhotoModels
            PhotoModel[] allPhoto = restTemplate.getForObject(JPH_API_URL, PhotoModel[].class);

            //check that allPhoto is present, otherwise an exception will be thrown
            assert allPhoto != null;

            //remove id from each Photo
            for (int i = 0; i < allPhoto.length; i++) {
                allPhoto[i].removeId();
            }

            //saves Photos to database and updates each Photo's id field to the saved database ID
            photoRepository.saveAll(Arrays.asList(allPhoto));

            //respond with the data that was just saved to the database
            return ResponseEntity.ok(allPhoto);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      POST one photo into database as an PhotoModel
    @PostMapping
    public ResponseEntity<?> uploadOnePhoto(@RequestBody PhotoModel newPhotoData) {
        try {
            newPhotoData.removeId();
//            TODO: Data validation on the new photo data (make sure fields are valid values)
            photoRepository.save(newPhotoData);
            return ResponseEntity.ok(newPhotoData);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //    GET one photo by ID (from SQL DB)
    @GetMapping("/sql/{id}")
    public ResponseEntity<?> getOnePhotoByID (@PathVariable String id) {
        try {
            //throws NumberFormatException if id is not an int
            int photoId = Integer.parseInt(id);
            System.out.println("Getting Photo With ID: " + id);

            //GET DATA FROM SQL (using repo)
            Optional<PhotoModel> foundPhoto = photoRepository.findById(photoId);

            if (foundPhoto.isEmpty()) return ResponseEntity.status(404).body("Photo Not Found With ID: " + id);
            //if (foundPhoto.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
            return ResponseEntity.ok(foundPhoto.get());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");
        }
        //TODO: reimplement exception throwing to handle 404 errors
//        catch (HttpClientErrorException e) {
//            return ResponseEntity.status(404).body("Photo Not Found With ID: " + id);
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //DELETE one photo by ID (from SQL DB) must make sure a photo with the given id already exist
    @DeleteMapping("/sql/{id}")
    public ResponseEntity<?> deleteOnePhotoByID (@PathVariable String id) {
        try {
            //throws NumberFormatException if id is not an int
            int photoId = Integer.parseInt(id);
            System.out.println("Getting Photo With ID: " + id);

            //GET DATA FROM SQL (using repo)
            Optional<PhotoModel> foundPhoto = photoRepository.findById(photoId);
            if (foundPhoto.isEmpty()) return ResponseEntity.status(404).body("Photo Not Found With ID: " + id);
            //if (foundPhoto.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

            photoRepository.deleteById(photoId);
            return ResponseEntity.ok(foundPhoto.get());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");
        }
        //TODO: reimplement exception throwing to handle 404 errors
//        catch (HttpClientErrorException e) {
//            return ResponseEntity.status(404).body("Photo Not Found With ID: " + id);
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //DELETE All photos (from SQL DB) - returns how many were just deleted
    @DeleteMapping("/sql/all")
    public ResponseEntity<?> deleteAllPhotosSQL() {
        try {
            long count = photoRepository.count();
            photoRepository.deleteAll();
            return ResponseEntity.ok("Deleted Photos: " + count);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      PUT one photo by ID (from SQL DB) - must make sure a photo with the given id already exist
    @PutMapping
    public ResponseEntity<?> updateOnePhoto(@RequestBody PhotoModel newPhotoData) {
        try {
            //TODO: Data validation on the new photo (make sure fields are valid values)
            photoRepository.save(newPhotoData);
            return ResponseEntity.ok(newPhotoData);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}

