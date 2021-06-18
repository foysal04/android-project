package com.example.afinal;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.media.Rating;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.io.Serializable;
import java.util.ArrayList;

public class Restaurant implements Parcelable {
    public String  restaurantName;
    public Location restaurantLocation;
    public float avgRating;
    public ArrayList<Review> restaurantReviews;
    public ImageView restaurantImage;
    boolean isCreated;

    protected Restaurant(Parcel in) {
        restaurantName = in.readString();
        restaurantLocation = in.readParcelable(Location.class.getClassLoader());
        avgRating = in.readFloat();
        isCreated = in.readByte() != 0;
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    private void initiateItems(){

        restaurantLocation = new Location(LocationManager.GPS_PROVIDER);

        isCreated = true;
        try {
            restaurantImage.setImageResource(R.drawable.yelp);
        } catch (Exception e){
            System.out.println(e);
        }

    }

    public Restaurant(String restaurantName, Float rating) {
        if(isCreated) return;
        this.restaurantName = restaurantName;
        restaurantReviews = new ArrayList<>();
//        restaurantReviews.add(new Review("Foysal", "This is awesome", 4));
        this.avgRating = rating;
        this.initiateItems();
    }

    public Location getRestaurantLocation() {
        return restaurantLocation;
    }

    public ImageView getRestaurantImage() {
        return restaurantImage;
    }

    public void setRestaurantImage(ImageView restaurantImage) {
        this.restaurantImage = restaurantImage;
    }

    public void setRestaurantLocation(Location restaurantLocation) {
        this.restaurantLocation = restaurantLocation;
    }

    public float getAverageRating() {
        return avgRating;
    }

    public void setAverageRating(float averageRating) {
        avgRating = averageRating;
    }

    public ArrayList<Review> getRestaurantReviews() {
        return restaurantReviews;
    }
    public void addReview(Review review){
        restaurantReviews.add(review);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(restaurantName);
        dest.writeParcelable(restaurantLocation, flags);
        dest.writeFloat(avgRating);
        dest.writeByte((byte) (isCreated ? 1 : 0));
    }
}
