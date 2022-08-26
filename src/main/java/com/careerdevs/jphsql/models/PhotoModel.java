package com.careerdevs.jphsql.models;

import javax.persistence.*;

@Entity
@Table(name="Photo")
public class PhotoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int albumId;
    private String title;
    private String url;
    private String thumbnailUrl;

    public int getId() {
        return id;
    }

    public void removeId () {
        id = 0;
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    //    "albumId": 1,
//            "id": 1,
//            "title": "accusamus beatae ad facilis cum similique qui sunt",
//            "url": "https://via.placeholder.com/600/92c952",
//            "thumbnailUrl": "https://via.placeholder.com/150/92c952"
}
