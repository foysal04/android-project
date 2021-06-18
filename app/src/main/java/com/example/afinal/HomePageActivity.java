package com.example.afinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePageActivity extends AppCompatActivity {
    private static final String TAG = "HomePageActivity";

    private ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();
    Database database = Database.getInstance();
    FirebaseFirestore firestore = database.getFirestore();
    CollectionReference mainRef = firestore.collection("Establishments");
    RecyclerView recyclerView;
    RestaurantRecyclerAdapter adapter;
    ArrayList<String> restNames = new ArrayList<>();

    private void initRestaurantRecyclerView(ArrayList<Restaurant> list){
        Log.d(TAG, "initRecyclerView: initialized recycler view");
        recyclerView = findViewById(R.id.recyclerViewHomePage);
        adapter = new RestaurantRecyclerAdapter(list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void onProfileButtonClick(View view){

        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);

        getRestaurantNames(new FirebaseCallback() {
            @Override
            public void onCallBack(List<Restaurant> list) {
                Log.i("Data1", list.toString());

                restaurantArrayList.addAll(list);
                initRestaurantRecyclerView(restaurantArrayList);
            }
        });

    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        String name = (String) parent.getItemAtPosition(position);
//        getRestaurantData(name, new FirebaseCallback() {
//            @Override
//            public void onCallBack(List<String> list) {
//                Log.i("Tmp", "This is just empty");
//            }
//        });
//    }

    private interface FirebaseCallback {
        void onCallBack(List<Restaurant> list);
    }

    private void getRestaurantNames(final FirebaseCallback firebaseCallback)
    {
        DocumentReference restNameRef = mainRef.document("Restaurant Names");

        restNameRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists())
                {
                    List<String> tmpList = new ArrayList<>();
                    Log.i("Snapshot", value.getData().toString());

                    Map<String, Object> map = new HashMap<>();
                    map = value.getData();
                    tmpList = (List<String>) map.get("Names");
                    List<Restaurant> send = new ArrayList<>();

                    for(int i = 0;i<tmpList.size();i++)
                        send.add(new Restaurant(tmpList.get(i), 5.0F));
                    firebaseCallback.onCallBack(send);
                }
            }
        });
    }

    private void getRestaurantData(String name, final FirebaseCallback firebaseCallback)
    {
        ArrayList<Review> reviews = new ArrayList<Review>();
        DocumentReference restDataRef = mainRef.document("Restaurants").collection(name).document("Info");
        CollectionReference reviewRef = restDataRef.collection("Reviews");

        reviewRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty())
                {
                    for(DocumentSnapshot doc: value.getDocuments())
                    {
                        reviews.add(new Review((String) doc.get("Title"), (String) doc.get("Body"), 5.0F));
                    }
                }
                restDataRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.exists())
                        {
                            Log.i("RestData", reviews.toString());
                            Map<String, Restaurant> map = new HashMap<>();;
                            Restaurant data = new Restaurant(name,5.0F);
//                            Log.i("data", data.getRestaurantLocation() + " " + data.getRestaurant());
                            Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                            intent.putExtra("data", data);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }
}