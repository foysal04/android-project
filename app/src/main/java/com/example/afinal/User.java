package com.example.afinal;

import android.media.Image;

import java.util.ArrayList;

public class User {
    String username;
    String email;
    Image profilePicture;
    ArrayList<Review> yourReviews;
    ArrayList<Restaurant> favouriteRestaurants;
    public User(String username) {
        this.username = username;
        yourReviews = new ArrayList<>();
        favouriteRestaurants = new ArrayList<>();
    }
    public User(String username, String email){
        this.username = username;
        yourReviews = new ArrayList<>();
        favouriteRestaurants = new ArrayList<>();
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Image getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Image profilePicture) {
        this.profilePicture = profilePicture;
    }

    public ArrayList<Review> getYourReviews() {
        return yourReviews;
    }

    public void setYourReviews(ArrayList<Review> yourReviews) {
        this.yourReviews = yourReviews;
    }

    public ArrayList<Restaurant> getFavouriteRestaurants() {
        return favouriteRestaurants;
    }

    public void setFavouriteRestaurants(ArrayList<Restaurant> favouriteRestaurants) {
        this.favouriteRestaurants = favouriteRestaurants;
    }
}
