package com.careerdevs.jphsql.models;

import javax.persistence.*;

@Entity
@Table(name="Album")
public class AlbumModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int userId;
    private String title;

    public int getId() {
        return id;
    }

    public void removeId () {
        id = 0;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }
}
