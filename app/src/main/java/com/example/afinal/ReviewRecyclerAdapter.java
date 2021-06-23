package com.example.afinal;

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

public class ReviewRecyclerAdapter extends RecyclerView.Adapter<ReviewRecyclerAdapter.ViewHolder> {
    private static final String TAG = "RestaurantRecyclerAdapt";

    private  ArrayList<Review> reviewArrayList = new ArrayList<>();
    private Context mContext;

    public ReviewRecyclerAdapter(ArrayList<Review> reviewArrayList, Context mContext) {
        this.reviewArrayList = reviewArrayList;
        this.mContext = mContext;
    }


    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review,parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull ReviewRecyclerAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + Integer.toString(position));
        holder.reviewerName.setText(reviewArrayList.get(position).username);
        holder.ratingBar.setRating(reviewArrayList.get(position).rating);
        holder.reviewText.setText(reviewArrayList.get(position).reviewText);

//        Log.i("Context", mContext.toString());
//        Log.i("Holder", holder.toString());
//        Log.i("Parent", holder.parentLayout.toString());
//        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("Context", mContext.toString());
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return  reviewArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView restaurantImage;
        TextView reviewerName;
        RatingBar ratingBar;
        TextView reviewText;
        ConstraintLayout parentLayout;
        public ViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            reviewerName = itemView.findViewById(R.id.reviewerNameReview);
            ratingBar = itemView.findViewById(R.id.reviewRatingBar);
            reviewText = itemView.findViewById(R.id.reviewText);
        }
    }
}
