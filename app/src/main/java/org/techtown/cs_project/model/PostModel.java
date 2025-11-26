package org.techtown.cs_project.model;

import com.google.firebase.Timestamp;

public class PostModel {
    private String title;
    private String bookTitle;
    private String building;
    private String price;
    private String content;
    private String userId;
    private Timestamp timestamp;

    public PostModel() {
    }

    public PostModel(String title, String bookTitle, String building, String price, String content, String userId, Timestamp timestamp) {
        this.title = title;
        this.bookTitle = bookTitle;
        this.building = building;
        this.price = price;
        this.content = content;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}