package com.careerdevs.jphsql.models;

import javax.persistence.*;

@Entity
@Table(name="Post")
public class PostModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int userId;
    private String title;
    private String body;

    public int getId() {
        return id;
    }

    public void removeId() {
        id = 0;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
