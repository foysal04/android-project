package com.example.afinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantActivity extends AppCompatActivity {

    Database database = Database.getInstance();
    FirebaseFirestore firestore = database.getFirestore();
    CollectionReference mainRef = firestore.collection("Establishments");
    RecyclerView recyclerView;
    String restaurantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        restaurantName = intent.getStringExtra("restaurant_name");
        getRestaurantData(new FirebaseCallback() {
            @Override
            public void onCallBack(List<Review> list) {
                initReviewRecyclerView((ArrayList<Review>) list);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRestaurantData(new FirebaseCallback() {
            @Override
            public void onCallBack(List<Review> list) {
                initReviewRecyclerView((ArrayList<Review>) list);
            }
        });
    }

    public void onLeaveReviewButtonCLick(View view){
        Intent intent = new Intent(getApplicationContext(), WriteReview.class);
        intent.putExtra("restaurant_name", restaurantName);
        startActivity(intent);
    }

    private void initReviewRecyclerView(ArrayList<Review> reviews){
        try{
            System.out.println("initiating restaurant recycler view");
            RecyclerView recyclerView = findViewById(R.id.recyclerViewRestaurantPage);
            ReviewRecyclerAdapter adapter = new ReviewRecyclerAdapter(reviews, getApplicationContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private interface FirebaseCallback {
        void onCallBack(List<Review> list);
    }

    private void getRestaurantData(final FirebaseCallback firebaseCallback)
    {
        ArrayList<Review> reviews = new ArrayList<Review>();
        DocumentReference restDataRef = mainRef.document("Restaurants").collection(restaurantName).document("Info");
        CollectionReference reviewRef = restDataRef.collection("Reviews");

        reviewRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty())
                {
                    for(DocumentSnapshot doc: value.getDocuments())
                        reviews.add(new Review((String) doc.get("Title"), (String) doc.get("Body"), 5.0F));

                    firebaseCallback.onCallBack(reviews);
                }
                restDataRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.exists())
                        {
                            List<String> tmpList = new ArrayList<>();
                            Log.i("Snapshot", value.getData().toString());

                            Map<String, Object> map = new HashMap<>();
                            map = value.getData();
                            tmpList = (List<String>) map.get("Names");

                            TextView restaurantNameTextView = (TextView) findViewById(R.id.restaurantNameRestaurantPage);
                            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarRestaurantPage);
                            ratingBar.setRating(5.0F);
                            restaurantNameTextView.setText(restaurantName);
                        }
                    }
                });
            }
        });
    }
}