package com.example.afinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();
    private ArrayList<Review> reviewArrayList = new ArrayList<>();

    String uid;

    Database database = Database.getInstance();
    FirebaseFirestore firestore = database.getFirestore();
    FirebaseUser mUser = database.getUser();

    CollectionReference mainRef = firestore.collection("Users");

    TextView nameView;

    @Override
    protected void onResume() {
        super.onResume();
        setUsername();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nameView = (TextView) findViewById(R.id.usernameTextField);
        uid = mUser.getUid();
        setUsername();
        getFavourites(new FirebaseCallbackFavourites() {
            @Override
            public void onCallBack(List<Restaurant> list) {
                initFavouriteRecyclerView((ArrayList<Restaurant>) list);
            }
        });
    }

    private void setUsername()
    {
        DocumentReference userRef = mainRef.document(uid);
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists())
                {
                    String username = (String) value.get("username");
                    Log.i("username", username);
                    nameView.setText(username);
                }
            }
        });
    }

    private void initFavouriteRecyclerView(ArrayList<Restaurant> list){
        if(!list.isEmpty()) {
            RecyclerView recyclerView = findViewById(R.id.profileRecyclerView);
            RestaurantRecyclerAdapter adapter = new RestaurantRecyclerAdapter(list, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public void onEditProfileButtonClick(View view){
        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    public void onFavouriteButtonClick(View view){
        try {
            getFavourites(new FirebaseCallbackFavourites() {
                @Override
                public void onCallBack(List<Restaurant> list) {
                    initFavouriteRecyclerView((ArrayList<Restaurant>) list);
                }
            });
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public void onReviewsButtonClick(View view){
        try{
            getReviews(new FirebaseCallbackReviews() {
                @Override
                public void onCallBack(List<Review> list) {
                    initReviewRecyclerView((ArrayList<Review>) list);
                }
            });
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public void initReviewRecyclerView(ArrayList<Review> list){
        if(!list.isEmpty()) {
            RecyclerView recyclerView = findViewById(R.id.profileRecyclerView);
            ReviewRecyclerAdapter adapter = new ReviewRecyclerAdapter(list, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private interface FirebaseCallbackReviews {
        void onCallBack(List<Review> list);
    }

    private interface FirebaseCallbackFavourites
    {
        void onCallBack(List<Restaurant> list);
    }

    private void getReviews(final FirebaseCallbackReviews firebaseCallback)
    {
        ArrayList<Review> reviews = new ArrayList<Review>();
        DocumentReference userRef = mainRef.document(uid);
        CollectionReference reviewRef = userRef.collection("Reviews");

        reviewRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty())
                {
                    for(DocumentSnapshot doc: value.getDocuments())
                        reviews.add(new Review((String) nameView.getText(), (String) doc.get("body"), 5.0F));

                    firebaseCallback.onCallBack(reviews);
                }
            }
        });
    }

    private void getFavourites(final FirebaseCallbackFavourites firebaseCallback)
    {
        ArrayList<Restaurant> favourites = new ArrayList<Restaurant>();
        DocumentReference userRef = mainRef.document(uid);
        CollectionReference favRef = userRef.collection("Favourites");

        favRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty())
                {
                    for(DocumentSnapshot doc: value.getDocuments())
                        favourites.add(new Restaurant((String) doc.get("Name"), 5.0F));

                    firebaseCallback.onCallBack(favourites);
                }
            }
        });
    }
}