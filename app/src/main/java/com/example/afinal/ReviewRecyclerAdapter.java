package com.example.afinal;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewRecyclerAdapter extends RecyclerView.Adapter<ReviewRecyclerAdapter.ViewHolder> {
    private static final String TAG = "RestaurantRecyclerAdapt";

    private ArrayList<Review> reviewArrayList = new ArrayList<>();
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
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext instanceof ProfileActivity) {
                    Log.i("Context", mContext.toString());

                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.getMenu().add("Edit review");
                    popupMenu.getMenu().add("Delete review");

                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getTitle().toString().equals("Delete review"))
                            {
                                Intent intent = new Intent(v.getContext(), DeleteReviewActivity.class);
                                String id = reviewArrayList.get(holder.getAdapterPosition()).getReviewId();
                                String name = reviewArrayList.get(holder.getAdapterPosition()).getRestaurantName();
                                intent.putExtra("id", id);
                                intent.putExtra("name", name);
                                Log.i("Review id", id);

                                v.getContext().startActivity(intent);
                            }

                            else
                            {
                                Intent intent = new Intent(v.getContext(), WriteReview.class);
                                intent.putExtra("request_code", "update");
                                intent.putExtra("restaurant_name", reviewArrayList.get(holder.getAdapterPosition()).getRestaurantName());
                                intent.putExtra("review_rating", reviewArrayList.get(holder.getAdapterPosition()).getRating());
                                intent.putExtra("review_body", reviewArrayList.get(holder.getAdapterPosition()).getText());
                                String id = reviewArrayList.get(holder.getAdapterPosition()).getReviewId();
                                intent.putExtra("id", id);
                                v.getContext().startActivity(intent);
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
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
            parentLayout = itemView.findViewById(R.id.reviewParentLayout);
//            restaurantImage = itemView.findViewById(R.id.)
        }
    }
}
