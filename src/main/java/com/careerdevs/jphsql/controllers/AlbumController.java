package com.careerdevs.jphsql.controllers;

import com.careerdevs.jphsql.models.AlbumModel;
import com.careerdevs.jphsql.repositories.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final String JPH_API_URL = "https://jsonplaceholder.typicode.com/albums";

    @Autowired
    private AlbumRepository albumRepository;

    //    Getting all album directly from JPH API
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

    //    Get all album stored in out local MySQL DB
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

    @PostMapping("/all")
    public ResponseEntity<?> uploadAllAlbumsToSQL(RestTemplate restTemplate) {

        try {

            AlbumModel[] allAlbums = restTemplate.getForObject(JPH_API_URL, AlbumModel[].class);

//            TODO: remove id from each album

            assert allAlbums != null;
            List<AlbumModel> savedAlbums = albumRepository.saveAll(Arrays.asList(allAlbums));

            return ResponseEntity.ok(savedAlbums);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadOneAlbum(@RequestBody AlbumModel newAlbumData) {

        try {

            newAlbumData.removeId();

//            TODO: Data validation on the new album data (make sure fields are valid values)

            AlbumModel savedAlbum = albumRepository.save(newAlbumData);

            return ResponseEntity.ok(savedAlbum);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}

