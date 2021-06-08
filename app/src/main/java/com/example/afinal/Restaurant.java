package com.example.afinal;

import android.location.Location;
import android.media.Rating;

import java.util.ArrayList;

public class Restaurant {
    public String restaurantName;
    public Location restaurantLocation;
    public Rating averageRating;
    public ArrayList<Review> restaurantReviews;
    boolean isCreated;
    public Restaurant(String restaurantName) {
        if(isCreated) return;
        this.restaurantName = restaurantName;
        restaurantReviews = new ArrayList<>();
        isCreated = true;
    }

    public Location getRestaurantLocation() {
        return restaurantLocation;
    }

    public void setRestaurantLocation(Location restaurantLocation) {
        this.restaurantLocation = restaurantLocation;
    }

    public Rating getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Rating averageRating) {
        this.averageRating = averageRating;
    }

    public ArrayList<Review> getRestaurantReviews() {
        return restaurantReviews;
    }


}
