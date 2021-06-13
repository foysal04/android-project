package com.example.afinal;

import android.media.Rating;

public class Review {
    public User user;
    public String username;
    public String reviewText;
    float rating;
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

//    public Review(User user, String reviewText, float rating) {
//        this.user = user;
//        this.reviewText = reviewText;
//        this.rating = rating;
//    }
    public Review(String username, String reviewText, float rating){
        this.username = username;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    public User getUser() {
        return user;
    }

    public String getReviewText() {
        return reviewText;
    }

    public float getRating() {
        return rating;
    }
}
