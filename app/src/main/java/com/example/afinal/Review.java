package com.example.afinal;

import android.media.Rating;

public class Review {
    public User user;
    public String reviewText;
    Rating rating;
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Review(User user, String reviewText, Rating rating) {
        this.user = user;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    public User getUser() {
        return user;
    }

    public String getReviewText() {
        return reviewText;
    }

    public Rating getRating() {
        return rating;
    }
}
