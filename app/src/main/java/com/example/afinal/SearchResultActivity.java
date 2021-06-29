package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    private ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    RestaurantRecyclerAdapter adapter;
    Intent intent;

    private void initRestaurantRecyclerView(ArrayList<Restaurant> list){

        recyclerView = findViewById(R.id.searchResultRecyclerView);
        adapter = new RestaurantRecyclerAdapter(list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        intent = getIntent();

        //initRestaurantRecyclerView(restaurantArrayList);
    }
}