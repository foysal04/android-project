package com.example.afinal;

import android.media.Rating;

public class Review {
    public User user;
    public String username;
    public String reviewText;
    Float rating;
    public Restaurant restaurant;
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

//    public Review(User user, String reviewText, float rating) {
//        this.user = user;
//        this.reviewText = reviewText;
//        this.rating = rating;
//    }
    public Review(String username, String reviewText, Float rating){
        this.username = username;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    public Review(User user, String reviewText, Float rating, Restaurant restaurant) {
        this.user = user;
        this.reviewText = reviewText;
        this.rating = rating;
        this.restaurant = restaurant;
        this.username = user.username;
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
