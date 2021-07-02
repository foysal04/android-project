package com.example.afinal;

import android.media.Rating;

public class Review {
    public User user;
    public String username;
    public String reviewText;
    Float rating;
    public Restaurant restaurant;
    String reviewId;
    String restaurantName;
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

    public Review(String username, String reviewText, Float rating, String id, String restaurantName) {
        this.username = username;
        this.reviewText = reviewText;
        this.rating = rating;
        this.reviewId = id;
        this.restaurantName = restaurantName;
    }

    public User getUser() {
        return user;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getText() {
        return reviewText;
    }

    public float getRating() {
        return rating;
    }

    public String getReviewId() {
        return reviewId;
    }
}
