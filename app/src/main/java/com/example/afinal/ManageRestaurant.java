package com.example.afinal;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageRestaurant {
    public static ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();

    public static void initArrayList(){
//        Database database = Database.getInstance();
//        FirebaseFirestore firestore = database.getFirestore();
//        CollectionReference mainRef = firestore.collection("Establishments");
//        DocumentReference restDataRef = mainRef.document("Restaurants").collection(name).document("Info");
//
//        restDataRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                if(value.exists())
//                {
//                    Log.i("RestData", reviews.toString());
//                    Map<String, Restaurant> map = new HashMap<>();;
//                    Restaurant data = new Restaurant(name, (String) value.get("Address"), reviews);
////                    Log.i("data", data.getRestaurantLocation() + " " + data.getRestaurantName());
//                    Intent intent = new Intent(getActivity(), RestaurantActivity.class);
//                    intent.putExtra("data", data);
//                    startActivity(intent);
//                }
//            }
//        });


//        Restaurant restaurant = new Restaurant("My shadher restaurant");
//        restaurant.setAverageRating(3.5f);
//        restaurantArrayList.add(restaurant);
//        Restaurant r2 = new Restaurant("My modhur Restaurant");
//        r2.setAverageRating(4);
//        restaurantArrayList.add(r2);
//        Restaurant r3 = new Restaurant("CSE chipa");
//        r3.setAverageRating(3.8f);
//        restaurantArrayList.add(r3);
//        Restaurant r4 = new Restaurant("Curzon");
//        restaurantArrayList.add(r4);
//        r4.setAverageRating(2);
//        for(Restaurant p : restaurantArrayList){
//            System.out.println("Size of" + p.restaurantName + p.restaurantReviews.size());
//        }
    }
}
