package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class  HomePageActivity extends AppCompatActivity {
    private static final String TAG = "HomePageActivity";

    private final int LIMIT = 20;
    int cnt = 0;

    private ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference mainRef = firestore.collection("Establishments");
    RecyclerView restaurantRecyclerView;
    RestaurantRecyclerAdapter restaurantRecyclerAdapter;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    EditText searchView;

    CollectionReference restRef = firestore.collection("Restaurants");
    Query query = restRef.orderBy("Rating", Query.Direction.DESCENDING).limit(LIMIT);

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);
        restaurantRecyclerView = findViewById(R.id.recyclerViewHomePage);

        searchView = (EditText) findViewById(R.id.searchView);
        getRestaurantNames(new RestaurantListCallback() {
            @Override
            public void onCallBack(List<Restaurant> list) {
                Log.i("Data1", list.toString());

                restaurantArrayList.addAll(list);
                initRestaurantRecyclerView(restaurantArrayList);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        restaurantRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    cnt += 1;

                    Log.i("cnt", String.valueOf(cnt));

                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            DocumentSnapshot lastVisible = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);

                            query = query.startAfter(lastVisible).limit(LIMIT);

                            ArrayList<Restaurant> arrayList = restaurantRecyclerAdapter.getRestaurantArrayList();

                            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    for (DocumentSnapshot docs : value.getDocuments()) {
                                        float rating = Float.parseFloat((String) docs.get("Rating"));
                                        restaurantArrayList.add(new Restaurant((String) docs.get("Display_name"), rating, (String) docs.get("Image")));
                                    }

                                    restaurantRecyclerAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void goToSearchPage(View view)
    {
        String query = searchView.getText().toString();

        if(query.isEmpty())
        {
            searchView.setError("Please put in a restaurant or tag");
            searchView.requestFocus();
        }

        else
        {
            Intent intent = new Intent(HomePageActivity.this, SearchResultActivity.class);
            intent.putExtra("query", query);
            startActivity(intent);
        }
    }

    public void onProfileButtonClick(View view){

        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private interface RestaurantListCallback {
        void onCallBack(List<Restaurant> list);
    }

    private void initRestaurantRecyclerView(ArrayList<Restaurant> list){
        Log.d(TAG, "initRecyclerView: initialized recycler view");
        restaurantRecyclerAdapter = new RestaurantRecyclerAdapter(list, this);
        restaurantRecyclerView.setAdapter(restaurantRecyclerAdapter);
        restaurantRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void getRestaurantNames(final RestaurantListCallback restaurantListCallback)
    {
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<Restaurant> send = new ArrayList<>();
                for(DocumentSnapshot docs: value.getDocuments())
                {
                    float rating = Float.parseFloat((String) docs.get("Rating"));
                    send.add(new Restaurant((String) docs.get("Display_name"), rating, (String) docs.get("Image")));
                }
                restaurantListCallback.onCallBack(send);
            }
        });
    }
}