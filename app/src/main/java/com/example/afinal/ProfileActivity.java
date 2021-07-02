package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    String uid;

    Database database = Database.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    CollectionReference mainRef = firestore.collection("Users");

    RecyclerView recyclerViewReview;
    ReviewRecyclerAdapter reviewAdapter;

    RecyclerView recyclerViewRestaurant;
    RestaurantRecyclerAdapter restaurantAdapter;

    TextView nameView;

    Button reviewsButton;
    Button favouritesButton;

    ImageView profilePicture;

    TextView noFavourites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nameView = (TextView) findViewById(R.id.usernameTextField);
        uid = mUser.getUid();
        setUsername();

        reviewsButton = (Button) findViewById(R.id.profileShowReviewButton);
        favouritesButton = (Button) findViewById(R.id.profileShowFavouritesButton);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        noFavourites = (TextView) findViewById(R.id.noFavouritesTextView);
        noFavourites.setVisibility(View.INVISIBLE);
//        noFavourites.setText(R.string.no_reviews);

        reviewsButton.setOnClickListener(this);
        favouritesButton.setOnClickListener(this);

        getReviews(new FirebaseCallbackReviews() {
            @Override
            public void onCallBack(List<Review> list) {
                initReviewRecyclerView((ArrayList<Review>) list);
                noFavourites.setText("You don't have any favourite yet");
            }
        });
        mainRef.document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    String imageDir = (String) value.get("Image");
//                    Log.i("imagedir", imageDir);
//                assert imageDir != null;
                    if (imageDir != null) {
                        StorageReference profileImageRef = database.getStorage().getReference().child(imageDir);

                        final long ONE_MEGABYTE = 1024 * 1024;
                        profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profilePicture.setImageBitmap(bmp);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainRef.document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    String imageDir = (String) value.get("Image");
//                    Log.i("imagedir", imageDir);
//                assert imageDir != null;
                    if (imageDir != null) {
                        StorageReference profileImageRef = database.getStorage().getReference().child(imageDir);

                        final long ONE_MEGABYTE = 1024 * 1024;
                        profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profilePicture.setImageBitmap(bmp);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finishAfterTransition();
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
            noFavourites.setVisibility(View.INVISIBLE);
            Log.i("empty", "not empty");
        }
    }

    public void onEditProfileButtonClick(View view){
        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    public void initReviewRecyclerView(ArrayList<Review> list){
        if (!list.isEmpty()) {
            recyclerViewReview = findViewById(R.id.profileRecyclerView);
            reviewAdapter = new ReviewRecyclerAdapter(list, this);
            recyclerViewReview.setAdapter(reviewAdapter);
            recyclerViewReview.setLayoutManager(new LinearLayoutManager(this));
            noFavourites.setVisibility(View.INVISIBLE);
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
                            if(!list.isEmpty()) {
                                noFavourites.setVisibility(View.INVISIBLE);
                                initReviewRecyclerView((ArrayList<Review>) list);
                            }
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
                            if(!list.isEmpty()) {
                                noFavourites.setVisibility(View.INVISIBLE);
                                initFavouriteRecyclerView((ArrayList<Restaurant>) list);
                            }
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
                    try {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            float rating = Float.parseFloat((String) doc.get("Rating"));
                            reviews.add(new Review((String) doc.get("Restaurant"), (String) doc.get("Body"),
                                    rating, (String) doc.get("Id"), (String) doc.get("Restaurant")));
                        }
                        if(reviews.isEmpty())
                            Log.i("empty", "is empty");
                        else
                            Log.i("empty", "not empty");
                        firebaseCallback.onCallBack(reviews);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getFavourites(final FirebaseCallbackFavourites firebaseCallback)
    {
        ArrayList<Restaurant> favourites = new ArrayList<Restaurant>();
        DocumentReference userRef = mainRef.document(uid);

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Map<String, Map<String, String>> data = (Map<String, Map<String, String>>) value.get("Favourites_data");

                if(data != null && !data.isEmpty())
                {
//                    Set<String> keys = data.keySet();
                    for(Map<String, String> values: data.values()) {
//                        Log.i("data", info.values().toString());
                        Float rating = Float.valueOf(values.get("Rating"));
                        favourites.add(new Restaurant(values.get("Name"), rating, values.get("Image")));
                    }
                    if(favourites.isEmpty())
                        Log.i("empty", "is empty");
                    else
                        Log.i("empty", "not empty");

                    firebaseCallback.onCallBack(favourites);
                }
            }
        });
    }
}