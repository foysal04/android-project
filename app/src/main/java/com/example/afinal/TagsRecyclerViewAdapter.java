package com.example.afinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TagsRecyclerViewAdapter extends RecyclerView.Adapter<TagsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> tags = new ArrayList<>();
    private Context mContext;

    public TagsRecyclerViewAdapter(ArrayList<String> tags, Context mContext) {
        this.tags = tags;
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tags_list_item,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TagsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.tagsTextView.setText(tags.get(position));
    }

    @Override
    public int getItemCount() {
        if(tags != null)
            return tags.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView tagsCardView;
        TextView tagsTextView;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tagsTextView = itemView.findViewById(R.id.tagsItemTextView);
        }
    }
}
