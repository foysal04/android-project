package com.example.afinal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RestaurantRecyclerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d(TAG, "onBindViewHolder: " + Integer.toString(position));
        holder.restaurantName.setText(restaurantArrayList.get(position).restaurantName);
        holder.ratingBar.setRating(restaurantArrayList.get(position).avgRating);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String imageDir = restaurantArrayList.get(position).getRestaurantImage();
        float rating = restaurantArrayList.get(position).getAverageRating();
        StorageReference profileImageRef = storageReference.child(imageDir);
//        isCreated = true;
//        Log.i("imagedir", restaurantName + " " + String.valueOf(profileImageRef));
        try {
            final long ONE_MEGABYTE = 1024 * 1024;
            profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    try {
//                        Log.i("bmp", restaurantName + " " +bmp.toString());
                        holder.restaurantImage.setImageBitmap(bmp);
//                        Log.d(TAG, "onSuccess: " + holder.restaurantImage.getWidth() + holder.restaurantImage.getHeight());;
                        holder.restaurantImage.setAdjustViewBounds(true);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("image data", "No image found");
                }
            });
//            restaurantImage.setImageResource(R.drawable.yelp);
        } catch (Exception e){
            System.out.println(e);
        }

        Log.i("Context", mContext.toString());
        Log.i("Holder", holder.toString());
        Log.i("Parent", holder.parentLayout.toString());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RestaurantActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.i("pos", String.valueOf(position));
                intent.putExtra("restaurant_name", (String) holder.restaurantName.getText());
                intent.putExtra("rating", String.valueOf(rating));
                intent.putExtra("image", imageDir);
                Log.i("name", (String) holder.restaurantName.getText());
                if(mContext instanceof HomePageActivity)
                    intent.putExtra("src", "HomePage");
                else if(mContext instanceof ProfileActivity)
                    intent.putExtra("src", "ProfilePage");
                else if(mContext instanceof SearchResultActivity)
                    intent.putExtra("src", "SearchPage");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantArrayList.size();
    }

    public void addRestaurant(Restaurant restaurant)
    {
        restaurantArrayList.add(restaurant);
    }

    public ArrayList<Restaurant> getRestaurantArrayList()
    {
        return restaurantArrayList;
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
