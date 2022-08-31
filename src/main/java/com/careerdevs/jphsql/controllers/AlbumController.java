package com.careerdevs.jphsql.controllers;

import com.careerdevs.jphsql.models.AlbumModel;
import com.careerdevs.jphsql.repositories.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    private final String JPH_API_URL = "https://jsonplaceholder.typicode.com/albums";
    @Autowired
    private AlbumRepository albumRepository;

    //      Getting all album directly from JPH API
    @GetMapping("/jph/all")
    public ResponseEntity<?> getAllAlbumsAPI(RestTemplate restTemplate) {
        try {
            AlbumModel[] allAlbums = restTemplate.getForObject(JPH_API_URL, AlbumModel[].class);
            return ResponseEntity.ok(allAlbums);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      Get all album stored in out local MySQL DB
    @GetMapping("/sql/all")
    public ResponseEntity<?> getAllAlbumsSQL() {
        try {
            ArrayList<AlbumModel> allAlbums = (ArrayList<AlbumModel>) albumRepository.findAll();
            return ResponseEntity.ok(allAlbums);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      Copy JPH SQL data in to jphsql database
    @PostMapping("/sql/all")
    public ResponseEntity<?> uploadAllAlbumsToSQL(RestTemplate restTemplate) {
        try {
            //retrieve data from JPH API and save to array of AlbumModels
            AlbumModel[] allAlbums = restTemplate.getForObject(JPH_API_URL, AlbumModel[].class);

            //check that allAlbums is present, otherwise an exception will be thrown
            assert allAlbums != null;

            //remove id from each album
            for (int i = 0; i < allAlbums.length; i++) {
                allAlbums[i].removeId();
            }

            //saves albums to database and updates each Album's id field to the saved database ID
            albumRepository.saveAll(Arrays.asList(allAlbums));

            //respond with the data that was just saved to the database
            return ResponseEntity.ok(allAlbums);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      POST one album into database as a AlbumModel
    @PostMapping
    public ResponseEntity<?> uploadOneAlbum(@RequestBody AlbumModel newAlbumData) {
        try {
            newAlbumData.removeId();
//            TODO: Data validation on the new album data (make sure fields are valid values)
            albumRepository.save(newAlbumData);
            return ResponseEntity.ok(newAlbumData);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //    GET one album by ID (from SQL DB)
    @GetMapping("/sql/{id}")
    public ResponseEntity<?> getOneAlbumByID (@PathVariable String id) {
        try {
            //throws NumberFormatException if id is not an int
            int albumId = Integer.parseInt(id);
            System.out.println("Getting Album With ID: " + id);

            //GET DATA FROM SQL (using repo)
            Optional<AlbumModel> foundAlbum = albumRepository.findById(albumId);

            if (foundAlbum.isEmpty()) return ResponseEntity.status(404).body("Album Not Found With ID: " + id);
            //if (foundAlbum.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

            return ResponseEntity.ok(foundAlbum.get());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");
        }
        //TODO: reimplement exception throwing to handle 404 errors
//        catch (HttpClientErrorException e) {
//            return ResponseEntity.status(404).body("Album Not Found With ID: " + id);
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      DELETE one album by ID (from SQL DB) must make sure a album with the given id already exist
    @DeleteMapping("/sql/{id}")
    public ResponseEntity<?> deleteOneAlbumByID (@PathVariable String id) {
        try {
            //throws NumberFormatException if id is not an int
            int albumId = Integer.parseInt(id);
            System.out.println("Getting Album With ID: " + id);

            //GET DATA FROM SQL (using repo)
            Optional<AlbumModel> foundAlbum = albumRepository.findById(albumId);
            if (foundAlbum.isEmpty()) return ResponseEntity.status(404).body("Album Not Found With ID: " + id);
            //if (foundAlbum.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

            albumRepository.deleteById(albumId);
            return ResponseEntity.ok(foundAlbum.get());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ID: " + id + ", is not a valid id. Must be a whole number");
        }
        //TODO: reimplement exception throwing to handle 404 errors
//        catch (HttpClientErrorException e) {
//            return ResponseEntity.status(404).body("Album Not Found With ID: " + id);
//        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      DELETE All albums (from SQL DB) - returns how many were just deleted
    @DeleteMapping("/sql/all")
    public ResponseEntity<?> deleteAllAlbumsSQL() {
        try {
            long count = albumRepository.count();
            albumRepository.deleteAll();
            return ResponseEntity.ok("Deleted Albums: " + count);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //      PUT one album by ID (from SQL DB) - must make sure an album with the given id already exist
    @PutMapping
    public ResponseEntity<?> updateOneAlbum(@RequestBody AlbumModel newAlbumData) {
        try {
            //TODO: Data validation on the new album (make sure fields are valid values)
            albumRepository.save(newAlbumData);
            return ResponseEntity.ok(newAlbumData);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}