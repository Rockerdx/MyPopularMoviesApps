package com.example.android.mypopularmoviesapp;

/**
 * Created by acer on 7/4/2017.
 */

public class ReviewModel {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    String id;
    String content;
    String author;
}
