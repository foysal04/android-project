package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    Database database = Database.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(database.getUser() != null)
            startActivity(new Intent(this, HomePageActivity.class));
        else
            startActivity(new Intent(this, LogInActivity.class));
        finishAfterTransition();
    }
}