package com.careerdevs.jphsql.models;

import javax.persistence.*;

@Entity
@Table(name="Comment")
public class CommentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int postId;
    private String name;
    private String email;
    private String body;

    public int getId() {
        return id;
    }

    public void removeId () {
        id = 0;
    }

    public int getPostId() {
        return postId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBody() {
        return body;
    }

    //    {
//        "postId": 1,
//            "id": 2,
//            "name": "quo vero reiciendis velit similique earum",
//            "email": "Jayne_Kuhic@sydney.com",
//            "body": "est natus enim nihil est dolore omnis voluptatem numquam\net omnis occaecati quod ullam at\nvoluptatem error expedita pariatur\nnihil sint nostrum voluptatem reiciendis et"
//    },
}
