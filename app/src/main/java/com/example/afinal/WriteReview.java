package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class WriteReview extends AppCompatActivity implements View.OnClickListener {

    Intent intent;
    Restaurant restaurant;
    int restaurantPos;
    Database database = Database.getInstance();
    FirebaseFirestore firestore = database.getFirestore();
    FirebaseUser mAuth = database.getUser();
    CollectionReference mainRef = firestore.collection("Establishments");

    String uid = mAuth.getUid();

    EditText writeReview;
    Button postReview;
    RatingBar ratingBar;

    String reviewBody;
    Float rating;

    String restaurantName;
    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.Q)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        intent = getIntent();
        restaurantName = intent.getStringExtra("restaurant_name");

        writeReview = (EditText) findViewById(R.id.writeReviewEditText);
        postReview = (Button) findViewById(R.id.postReviewButton);
        ratingBar = (RatingBar) findViewById(R.id.ratingBarWriteReview);

        postReview.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finishAfterTransition();
    }

    @Override
    public void onClick(View v) {
        reviewBody = writeReview.getText().toString();
        rating = ratingBar.getRating();

        if(reviewBody.isEmpty())
        {
            writeReview.setError("Review body cannot be empty");
            writeReview.requestFocus();
            return;
        }

        if(ratingBar.getRating()<1f){
            Toast.makeText(getApplicationContext(),"Please add a rating", Toast.LENGTH_LONG).show();
            return;
        }
        DocumentReference restDataRef = mainRef.document("Restaurants")
                                                .collection(restaurantName)
                                                .document("Info");

        CollectionReference reviewRef = restDataRef.collection("Reviews");

        DocumentReference userRef = firestore.collection("Users")
                                            .document(uid);

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists())
                {
                    Map<String, Object> data = new HashMap<>();
                    data = value.getData();

                    String username = (String) data.get("username");
                    Review review = new Review(username, reviewBody, rating);

                    data.clear();
                    data.put("restaurant", restaurantName);
                    data.put("rating", rating);
                    data.put("body", reviewBody);

                    userRef.collection("Reviews")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.i("DocumentSnapshot ID: ", documentReference.getId());
                                }
                            });
                    data.clear();
                    data.put("username", username);
                    data.put("rating", rating);
                    data.put("body", reviewBody);

                    reviewRef.add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.i("DocumentSnapshot ID: ", documentReference.getId());
                                }
                            });
                }
            }
        });

        Intent intent = new Intent(this, RestaurantActivity.class);
        intent.putExtra("restaurant_name", restaurantName);
        startActivity(intent);
    }
}