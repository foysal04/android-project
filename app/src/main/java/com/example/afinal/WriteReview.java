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
    Database database = Database.getInstance();
    FirebaseFirestore firestore = database.getFirestore();
    CollectionReference mainRef = firestore.collection("Establishments");

    String uid = database.getFirebaseAuth().getCurrentUser().getUid();
    EditText writeReview;
    Button postReview;
    RatingBar ratingBar;
    String reviewBody;
    Float rating;
    String restaurantName;
    String requestCode;
    String username;

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.Q)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        intent = getIntent();
        restaurantName = intent.getStringExtra("restaurant_name");
        requestCode = intent.getStringExtra("request_code");

        writeReview = (EditText) findViewById(R.id.writeReviewEditText);
        postReview = (Button) findViewById(R.id.postReviewButton);
        ratingBar = (RatingBar) findViewById(R.id.ratingBarWriteReview);

        Log.i("request", requestCode);

        if(requestCode.equals("update"))
        {
            rating = intent.getFloatExtra("review_rating", 0.0F);
            reviewBody = intent.getStringExtra("review_body");
            restaurantName = intent.getStringExtra("restaurant_name");

            ratingBar.setRating(rating);
            writeReview.setText(reviewBody);
        }

        postReview.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finishAfterTransition();
    }

    @Override
    public void onClick(View v) {
        Map<String, Object> data = new HashMap<>();
//        DocumentReference restDataRef = mainRef.document("Restaurants")
//                .collection(restaurantName)
//                .document("Info");
//        CollectionReference reviewRef = restDataRef.collection("Reviews");

        DocumentReference restRef = firestore.collection("Restaurants").document(restaurantName);
        CollectionReference reviewRef = restRef.collection("Reviews");
        DocumentReference userRef = firestore.collection("Users").document(uid);

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

        if(requestCode.equals("write")) {
            Log.i("req_code", requestCode);
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    Log.i("USer name", value.getData().toString());
                    data.put("Name", (String) value.get("username"));
                    data.put("Rating", rating.toString());
                    data.put("Body", reviewBody);

                    reviewRef.add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.i("Added to restaurant: ", documentReference.getId());
                                    data.clear();
                                    data.put("Restaurant", restaurantName);
                                    data.put("Rating", rating.toString());
                                    data.put("Body", reviewBody);
                                    data.put("Id", documentReference.getId());

                                    userRef.collection("Reviews")
                                            .document(documentReference.getId())
                                            .set(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.i("Review added to user list", documentReference.getId());
                                                }
                                            });
                                }
                            });
                }
            });
        }

        else if(requestCode.equals("update"))
        {
            Log.i("req_code", requestCode);
            String id = intent.getStringExtra("id");
            Log.i("req_id", id);
            data.put("Name", username);
            data.put("Rating", rating);
            data.put("Body", reviewBody);

            reviewRef.document(id)
                    .update("Body", reviewBody, "Rating", rating.toString());

            userRef.collection("Reviews")
                    .document(id)
                    .update("Body", reviewBody, "Rating", rating.toString());

            startActivity(new Intent(WriteReview.this, ProfileActivity.class));
        }
        finish();
    }
}