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
    Context mContext;
    public String restaurantName;
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

    public void initiateItems(){

        restaurantLocation = new Location(LocationManager.GPS_PROVIDER);
        restaurantReviews = new ArrayList<>();

    }

    public Restaurant(String restaurantName) {
        if(isCreated) return;
        this.restaurantName = restaurantName;
        restaurantReviews = new ArrayList<>();
        isCreated = true;
        initiateItems();
    }

    public Location getRestaurantLocation() {
        return restaurantLocation;
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
