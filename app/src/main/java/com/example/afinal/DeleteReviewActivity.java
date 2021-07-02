package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteReviewActivity extends AppCompatActivity implements View.OnClickListener {
    String id;
    String name;
    Database database = Database.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_review);

        Button yesButton = (Button) findViewById(R.id.yesReviewDeleteButton);
        Button noButton = (Button) findViewById(R.id.noReviewDeleteButton);

        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.yesReviewDeleteButton:
            {
                FirebaseFirestore firestore = database.getFirestore();

                firestore.collection("Restaurants")
                        .document(name)
                        .collection("Reviews")
                        .document(id)
                        .delete();

                String uid = database.getFirebaseAuth().getCurrentUser().getUid();

                firestore.collection("Users")
                        .document(uid)
                        .collection("Reviews")
                        .document(id)
                        .delete();

                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            }break;

            case R.id.noReviewDeleteButton:
            {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            }break;
        }
    }
}