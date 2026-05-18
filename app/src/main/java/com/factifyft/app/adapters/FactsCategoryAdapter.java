package com.factifyft.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.factifyft.app.R;
import com.factifyft.app.data.FactCategory;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class FactsCategoryAdapter extends RecyclerView.Adapter<FactsCategoryAdapter.ViewHolder> {

    private final List<FactCategory> categories;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(FactCategory category);
    }

    public FactsCategoryAdapter(List<FactCategory> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fact_category_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FactCategory category = categories.get(position);
        holder.title.setText(category.getName());
        holder.image.setImageResource(category.getImageRes());

        holder.card.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final ImageView image;
        final MaterialCardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.categoryTitle);
            image = itemView.findViewById(R.id.categoryImage);
            card = itemView.findViewById(R.id.categoryCard);
        }
    }
}
