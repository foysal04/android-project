package com.example.afinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();
    private ArrayList<Review> reviewArrayList = new ArrayList<>();

    String uid;

    Database database = Database.getInstance();
    FirebaseFirestore firestore = database.getFirestore();
    FirebaseUser mUser = database.getFirebaseAuth().getCurrentUser();

    CollectionReference mainRef = firestore.collection("Users");

    RecyclerView recyclerViewReview;
    ReviewRecyclerAdapter reviewAdapter;

    RecyclerView recyclerViewRestaurant;
    RestaurantRecyclerAdapter restaurantAdapter;

    TextView nameView;

    Button reviewsButton;
    Button favouritesButton;

    @Override
    protected void onResume() {
        super.onResume();
        setUsername();

        getFavourites(new FirebaseCallbackFavourites() {
            @Override
            public void onCallBack(List<Restaurant> list) {
                initFavouriteRecyclerView((ArrayList<Restaurant>) list);
            }
        });

        getReviews(new FirebaseCallbackReviews() {
            @Override
            public void onCallBack(List<Review> list) {
                initReviewRecyclerView((ArrayList<Review>) list);
            }
        });
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        finishAfterTransition();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nameView = (TextView) findViewById(R.id.usernameTextField);
        uid = mUser.getUid();
        setUsername();

        reviewsButton = (Button) findViewById(R.id.profileShowReviewButton);
        favouritesButton = (Button) findViewById(R.id.profileShowFavouritesButton);

        reviewsButton.setOnClickListener(this);
        favouritesButton.setOnClickListener(this);

        getReviews(new FirebaseCallbackReviews() {
            @Override
            public void onCallBack(List<Review> list) {
                initReviewRecyclerView((ArrayList<Review>) list);
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
            recyclerViewRestaurant = (RecyclerView) findViewById(R.id.profileRecyclerView);
            restaurantAdapter = new RestaurantRecyclerAdapter(list, this);
            recyclerViewRestaurant.setAdapter(restaurantAdapter);
            recyclerViewRestaurant.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public void onEditProfileButtonClick(View view){
        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    public void initReviewRecyclerView(ArrayList<Review> list){
        try {
            if (!list.isEmpty()) {
                recyclerViewReview = findViewById(R.id.profileRecyclerView);
                reviewAdapter = new ReviewRecyclerAdapter(list, getApplicationContext());
                recyclerViewReview.setAdapter(reviewAdapter);
                recyclerViewReview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.profileShowReviewButton:
            {
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
            }break;

            case R.id.profileShowFavouritesButton:
            {
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
            }break;
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
                    for(DocumentSnapshot doc: value.getDocuments()) {
                        Float _pubRating = ((Number) Objects.requireNonNull(doc.get("Rating"))).floatValue();
                        reviews.add(new Review((String) doc.get("Restaurant"), (String) doc.get("Body"), _pubRating));
                    }
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

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<String> data = (ArrayList<String>) value.get("Favourites");

               if(data!=null){
                   if(!data.isEmpty())
                   {
                       for(int i = 0;i<data.size();i++)
                           favourites.add(new Restaurant(data.get(i), 3.0F));

                       firebaseCallback.onCallBack(favourites);
                   }
               }
            }
        });
    }
}