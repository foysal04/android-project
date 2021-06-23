package com.example.afinal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RestaurantRecyclerAdapter extends RecyclerView.Adapter<RestaurantRecyclerAdapter.ViewHolder> {
    private static final String TAG = "RestaurantRecyclerAdapt";

    private  ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();
    private Context mContext;

    public RestaurantRecyclerAdapter(ArrayList<Restaurant> restaurantArrayList, Context mContext) {
        this.restaurantArrayList = restaurantArrayList;
        this.mContext = mContext;
    }


    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_recycler_view,parent, false);
        ViewHolder holder = new ViewHolder(view);
        return  holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RestaurantRecyclerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d(TAG, "onBindViewHolder: " + Integer.toString(position));
        holder.restaurantName.setText(restaurantArrayList.get(position).restaurantName);
        holder.ratingBar.setRating(restaurantArrayList.get(position).avgRating);

        Log.i("Context", mContext.toString());
        Log.i("Holder", holder.toString());
        Log.i("Parent", holder.parentLayout.toString());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RestaurantActivity.class);
                Log.i("pos", String.valueOf(position));
                intent.putExtra("restaurant_name", (String) holder.restaurantName.getText());
                Log.i("name", (String) holder.restaurantName.getText());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  restaurantArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView restaurantImage;
        TextView restaurantName;
        RatingBar ratingBar;
        ConstraintLayout parentLayout;
        public ViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.restaurantImageRestaurantRecyclerView);
            restaurantName = itemView.findViewById(R.id.restaurantNameRestaurantRecyclerView);
            ratingBar = itemView.findViewById(R.id.ratingBarRestaurantRecyclerView);
            parentLayout = itemView.findViewById(R.id.restaurantRecyclerParentLayout);

        }
    }
}
