package com.example.learnly_skillbit_app.showingFactsScreen;


import static androidx.core.util.TypedValueCompat.dpToPx;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnly_skillbit_app.R;

import java.util.List;

public class FactsCategoryAdapter extends RecyclerView.Adapter<FactsCategoryAdapter.ViewHolder> {

    private Context context;
    private List<FactCategory> categories;
    private OnCategoryClickListener listener;

    private int width,height;


    public interface OnCategoryClickListener {
        void onCategoryClick(FactCategory category);
    }

    public FactsCategoryAdapter(Context context, List<FactCategory> categories, OnCategoryClickListener listener,int width,int height) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
        this.width=width;
        this.height=height;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fact_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FactCategory category = categories.get(position);
        holder.title.setText(category.getName());
        holder.image.setImageResource(category.getImageRes());

        //set fixed card sizes
        int cardWidth=(int) (width/2.0) - dpToPx(16,context);
        int cardHeight=dpToPx(200,context);
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
        TextView title;
        ImageView image;
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.categoryTitle);
            image = itemView.findViewById(R.id.categoryImage);
            card = itemView.findViewById(R.id.categoryCard);
        }
    }
}

