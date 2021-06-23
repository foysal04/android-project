package com.example.afinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ContentInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class RestaurantActivity extends AppCompatActivity implements View.OnClickListener{

    Database database = Database.getInstance();
    FirebaseFirestore firestore = database.getFirestore();
    CollectionReference mainRef = firestore.collection("Establishments");
    RecyclerView recyclerView;
    String restaurantName;
    String uid;
    Button addReviewButton;
    Button addFavouriteButton;
    Button seeInfoButton;

    ReviewRecyclerAdapter adapter;

//    StorageReference storageReference =

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        addReviewButton = (Button) findViewById(R.id.addReviewButton);
        addFavouriteButton = (Button) findViewById(R.id.addToFavouritesButton);
        seeInfoButton = (Button) findViewById(R.id.locationButton);

        addReviewButton.setOnClickListener(this);
        addFavouriteButton.setOnClickListener(this);
        seeInfoButton.setOnClickListener(this);

        restaurantName = intent.getStringExtra("restaurant_name");
        getRestaurantData(new FirebaseCallback() {
            @Override
            public void onCallBack(List<Review> list) {
                initReviewRecyclerView((ArrayList<Review>) list);
            }
        });

        uid = Objects.requireNonNull(database.getFirebaseAuth().getCurrentUser()).getUid();
        firestore.collection("Users")
                .document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        Map<String, Object> data = value.getData();

                        if(data.containsKey("Favourites"))
                        {
                            ArrayList<String> favourites = (ArrayList<String>) data.get("Favourites");

                            if(favourites.contains(restaurantName)) {
                                addFavouriteButton.setText("Unfavourite");
                            }
                            else {
                                addFavouriteButton.setText("Favourite");
                            }
                        }
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

    private void initReviewRecyclerView(ArrayList<Review> reviews){
        try{
            System.out.println("initiating restaurant recycler view");
            recyclerView = findViewById(R.id.recyclerViewRestaurantPage);
            adapter = new ReviewRecyclerAdapter(reviews, getApplicationContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        } catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addReviewButton:
            {
                Intent intent = new Intent(getApplicationContext(), WriteReview.class);
                intent.putExtra("restaurant_name", restaurantName);
                startActivity(intent);
            }break;

            case R.id.addToFavouritesButton:
            {
                if(addFavouriteButton.getText() == "Favourite") {
                    firestore.collection("Users")
                            .document(uid)
                            .update("Favourites", FieldValue.arrayUnion(restaurantName));

                    Toast.makeText(this, restaurantName+" added to favourites", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    firestore.collection("Users")
                            .document(uid)
                            .update("Favourites", FieldValue.arrayRemove(restaurantName));

                    Toast.makeText(this, restaurantName+" removed from favourites", Toast.LENGTH_SHORT).show();
                }
            }break;

            case R.id.locationButton:
            {
                DocumentReference restDataRef = mainRef.document("Restaurants")
                                                        .collection(restaurantName)
                                                        .document("Info");

                restDataRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        String address = (String) value.get("Address");

                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        intent.putExtra("address", address);
                        intent.putExtra("name", restaurantName);

                        startActivity(intent);
                    }
                });
            }break;
        }
    }

    private interface FirebaseCallback {
        void onCallBack(List<Review> list);
    }

    // Rework data and comeback here to resolve issues 1,2 and 3
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
                    for(DocumentSnapshot doc: value.getDocuments()) {
                        Float _pubRating = ((Number) Objects.requireNonNull(doc.get("Rating"))).floatValue();
                        reviews.add(new Review((String) doc.get("Name"), (String) doc.get("Body"), _pubRating));
                        Log.i("data", doc.getData().toString());
                    }

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
                            ratingBar.setRating(3.0F);
                            restaurantNameTextView.setText(restaurantName);
                        }
                    }
                });
            }
        });
    }
}