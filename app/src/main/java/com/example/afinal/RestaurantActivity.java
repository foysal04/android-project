package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class RestaurantActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference mainRef = firestore.collection("Establishments");
    RecyclerView recyclerView;
    String restaurantName;
    String rating;
    String imageDir;

    String uid;
    Button addReviewButton;
    Button addFavouriteButton;
    Button seeInfoButton;
    ImageView profileImage;

    Intent intent;

    String origin = "";
    String destination = "";

    ReviewRecyclerAdapter adapter;
    TagsRecyclerViewAdapter tagsAdapter;
    RecyclerView tagsView;

    LocationManager locationManager;
    LocationListener locationListener;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    List<Address> addresses;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        addReviewButton = (Button) findViewById(R.id.addReviewButton);
        addFavouriteButton = (Button) findViewById(R.id.addToFavouritesButton);
        seeInfoButton = (Button) findViewById(R.id.locationButton);
        profileImage = (ImageView) findViewById(R.id.restaurantProfilePicture);

        recyclerView = findViewById(R.id.recyclerViewRestaurantPage);

        addReviewButton.setOnClickListener(this);
        addFavouriteButton.setOnClickListener(this);
        seeInfoButton.setOnClickListener(this);

        restaurantName = intent.getStringExtra("restaurant_name");
        imageDir = intent.getStringExtra("image");
        rating = intent.getStringExtra("rating");
        getRestaurantData(new FirebaseCallback() {
            @Override
            public void onCallBack(List<Review> list) {
                initReviewRecyclerView((ArrayList<Review>) list);
            }
        });

        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        firestore.collection("Users").document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Map<String, Object> data = value.getData();

                        if (data.containsKey("Favourites_data")) {
                            Log.i("hasdata", "Has data");
                            Map<String, Map<String, String>> favourites = (Map<String, Map<String, String>>) data.get("Favourites_data");

                            if (favourites.containsKey(restaurantName)) {
                                addFavouriteButton.setText("Unfavourite");
                            } else {
                                addFavouriteButton.setText("Favourite");
                            }
                        }
                    }
                });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        getRestaurantData(new FirebaseCallback() {
            @Override
            public void onCallBack(List<Review> list) {
                initReviewRecyclerView((ArrayList<Review>) list);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String src = intent.getStringExtra("src");

//        Log.i("src", src);
        if(src != null) {
            if (src.equals("HomePage"))
                startActivity(new Intent(RestaurantActivity.this, HomePageActivity.class));

            else if (src.equals("ProfilePage"))
                startActivity(new Intent(RestaurantActivity.this, ProfileActivity.class));

            else if (src.equals("SearchPage"))
                startActivity(new Intent(RestaurantActivity.this, SearchResultActivity.class));
        }
        finish();
    }

    private void initReviewRecyclerView(ArrayList<Review> reviews){
        try{
            System.out.println("initiating restaurant recycler view");

            adapter = new ReviewRecyclerAdapter(reviews, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void initTags(ArrayList<String> tags){
        try{
            System.out.println("initialising tags");
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            tagsView = findViewById(R.id.tagsRecyclerView);
            tagsAdapter = new TagsRecyclerViewAdapter(tags, getApplicationContext());
            tagsView.setLayoutManager(layoutManager);
            tagsView.setAdapter(tagsAdapter);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private interface FirebaseCallback {
        void onCallBack(List<Review> list);
    }

    // Rework data and comeback here to resolve issues 1,2 and 3
    private void getRestaurantData(final FirebaseCallback firebaseCallback)
    {
        ArrayList<Review> reviews = new ArrayList<Review>();
//        DocumentReference restDataRef = mainRef.document("Restaurants").collection(restaurantName).document("Info");
//        CollectionReference reviewRef = restDataRef.collection("Reviews");

        DocumentReference restRef = firestore.collection("Restaurants").document(restaurantName);
        CollectionReference reviewRef = restRef.collection("Reviews");

        reviewRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot value = task.getResult();

                if(!value.isEmpty())
                {
                    try {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            float rating = Float.parseFloat((String) doc.get("Rating"));
                            reviews.add(new Review((String) doc.get("Name"), (String) doc.get("Body"), rating));
                            Log.i("data", doc.getData().toString());
                        }

                        firebaseCallback.onCallBack(reviews);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                restRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.exists())
                        {
                            Log.i("Snapshot", value.getData().toString());

                            TextView restaurantNameTextView = (TextView) findViewById(R.id.restaurantNameRestaurantPage);
                            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarRestaurantPage);
                            float rating_float = Float.parseFloat(rating);
                            ratingBar.setRating(rating_float);
                            restaurantNameTextView.setText(restaurantName);
                            StorageReference profileImageRef = storageReference.child(imageDir);

                            ArrayList<String> tags = (ArrayList<String>) value.get("Tags");
                            initTags(tags);

                            final long ONE_MEGABYTE = 1024 * 1024;
                            profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    profileImage.setImageBitmap(bmp);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getApplicationContext(), "No such file or path found!!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
            }
        });

//        reviewRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if(!value.isEmpty())
//                {
//                    try {
//                        for (DocumentSnapshot doc : value.getDocuments()) {
//                            float rating = Float.parseFloat((String) doc.get("Rating"));
//                            reviews.add(new Review((String) doc.get("Name"), (String) doc.get("Body"), rating));
//                            Log.i("data", doc.getData().toString());
//                        }
//
//                        firebaseCallback.onCallBack(reviews);
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//                restRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                        if(value.exists())
//                        {
//                            Log.i("Snapshot", value.getData().toString());
//
//                            TextView restaurantNameTextView = (TextView) findViewById(R.id.restaurantNameRestaurantPage);
//                            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarRestaurantPage);
//                            float rating_float = Float.parseFloat(rating);
//                            ratingBar.setRating(rating_float);
//                            restaurantNameTextView.setText(restaurantName);
//                            StorageReference profileImageRef = storageReference.child(imageDir);
//
//                            ArrayList<String> tags = (ArrayList<String>) value.get("Tags");
//                            initTags(tags);
//
//                            final long ONE_MEGABYTE = 1024 * 1024;
//                            profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                                @Override
//                                public void onSuccess(byte[] bytes) {
//                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                                    profileImage.setImageBitmap(bmp);
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception exception) {
//                                    Toast.makeText(getApplicationContext(), "No such file or path found!!", Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                    }
//                });
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addReviewButton:
            {
                Intent intent = new Intent(getApplicationContext(), WriteReview.class);
                intent.putExtra("restaurant_name", restaurantName);
                intent.putExtra("request_code", "write");
                intent.putExtra("src", "Restaurant");
                startActivity(intent);
            }break;

            case R.id.addToFavouritesButton:
            {
                DocumentReference userRef = firestore.collection("Users").document(uid);
                Log.i("label", addFavouriteButton.getText().toString());
                if(addFavouriteButton.getText().toString().equals("Favourite")) {
                    Log.i("favs", "yayya");

                    Map<String,String> inside = new HashMap<>();
                    inside.put("Name", restaurantName);
                    inside.put("Image", imageDir);
                    inside.put("Rating", rating);

                    Log.i("rest data", inside.toString());
                    userRef.update("Favourites_data."+restaurantName, inside);

//                    firestore.collection("Users")
//                            .document(uid)
//                            .update("Favourites", FieldValue.arrayUnion(restaurantName));

                    addFavouriteButton.setText((CharSequence) "Favourite");
                    Toast.makeText(this, restaurantName+" added to favourites", Toast.LENGTH_SHORT).show();
                }

                else
                {
//                    firestore.collection("Users")
//                            .document(uid)
//                            .update("Favourites", FieldValue.arrayRemove(restaurantName));

                    userRef.update("Favourites_data."+restaurantName, FieldValue.delete());
                    addFavouriteButton.setText((CharSequence) "Unfavourite");
                    Toast.makeText(this, restaurantName+" removed from favourites", Toast.LENGTH_SHORT).show();
                }

//                finish();
            }break;

            case R.id.locationButton:
            {
                DocumentReference restRef = firestore.collection("Restaurants").document(restaurantName);
                CollectionReference reviewRef = restRef.collection("Reviews");

                restRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        String address = (String) value.get("Address");
                        Log.i("dest", destination);

                        Intent intent = new Intent(RestaurantActivity.this, MapsActivity.class);
                        intent.putExtra("address", address);
                        intent.putExtra("name", restaurantName);

                        startActivity(intent);
                    }
                });
            }break;
        }
    }
}