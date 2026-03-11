package com.factifyft.app.showingFactsScreen;


import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.factifyft.app.R;

import java.util.List;

public class FactsCategoryAdapter extends RecyclerView.Adapter<FactsCategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<FactCategory> categories;
    private final OnCategoryClickListener listener;

    private final int width; //height removed


    public interface OnCategoryClickListener {
        void onCategoryClick(FactCategory category);
    }

    public FactsCategoryAdapter(Context context, List<FactCategory> categories, OnCategoryClickListener listener,int width) {  //height removed
        this.context = context;
        this.categories = categories;
        this.listener = listener;
        this.width=width;
//       this.height=height;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fact_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FactCategory category = categories.get(position);
        holder.title.setText(category.getName());
        holder.image.setImageResource(category.getImageRes());

        //set fixed card sizes
        int cardWidth=(int) (width/2.0) - dpToPx(16,context);
        int cardHeight=dpToPx(160,context);
        ViewGroup.LayoutParams params=holder.card.getLayoutParams();
        params.width=cardWidth;
        params.height=cardHeight;
        holder.card.setLayoutParams(params);

        holder.card.setOnClickListener(v -> listener.onCategoryClick(category));
    }

    private int dpToPx(int i, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, context.getResources().getDisplayMetrics());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final ImageView image;
        final CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.categoryTitle);
            image = itemView.findViewById(R.id.categoryImage);
            card = itemView.findViewById(R.id.categoryCard);
        }
    }
}

