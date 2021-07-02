package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SearchResultActivity extends AppCompatActivity {

    private ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    RestaurantRecyclerAdapter adapter;
    Intent intent;
    EditText searchResult;
    String query;
    Client client;
    Index index;

    private void initRestaurantRecyclerView(ArrayList<Restaurant> list){
        adapter = new RestaurantRecyclerAdapter(list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private interface RestaurantCallback {
        void onCallBack(List<Restaurant> list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        recyclerView = findViewById(R.id.searchResultRecyclerView);
        intent = getIntent();
        query = intent.getStringExtra("query");

        client = new Client("ADTUKLENRO", "fe5c4d45c43bda0d6978a0926ec22a4e");
        index = client.getIndex("Uni_project");

        getQuery(new RestaurantCallback() {
            @Override
            public void onCallBack(List<Restaurant> list) {
                initRestaurantRecyclerView((ArrayList<Restaurant>) list);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void getQuery(RestaurantCallback restaurantCallback)
    {
        CollectionReference nameRef = FirebaseFirestore.getInstance()
                .collection("Restaurants");

        String queryLower = query.toLowerCase();
        Log.i("query", query);

        Query nameQuery = nameRef.whereGreaterThanOrEqualTo("Query_name", queryLower);
        Query tagQuery = nameRef.whereArrayContains("Tags", queryLower);

//        Query foo = nameRef.
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        nameQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete())
                {
                    Log.i("result", String.valueOf(task.getResult().size()));
                    if(!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Float rating = Float.parseFloat((String) document.get("Rating"));
                            restaurants.add(new Restaurant((String) document.get("Display_name"), rating, (String) document.get("Image")));
                        }
                        restaurantCallback.onCallBack(restaurants);
                    }

                    else
                    {
                        tagQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(!task.getResult().isEmpty())
                                {
                                    ArrayList<String> names = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        Float rating = Float.parseFloat((String) document.get("Rating"));
                                        restaurants.add(new Restaurant((String) document.get("Display_name"), rating, (String) document.get("Image")));
                                    }
                                    restaurantCallback.onCallBack(restaurants);
                                }

                                else
                                {
                                    Toast.makeText(SearchResultActivity.this, "No results!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}