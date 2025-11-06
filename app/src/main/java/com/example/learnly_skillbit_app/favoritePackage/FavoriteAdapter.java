package com.example.learnly_skillbit_app.favoritePackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnly_skillbit_app.R;
import com.example.learnly_skillbit_app.customToast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.VH> {

    private final Context context;
    private final List<String> favorites;
    private final SharedPreferences prefs;

    public FavoriteAdapter(Context context, List<String> favorites) {
        this.context = context;
        this.favorites = favorites;
        prefs = context.getSharedPreferences("facts_prefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String key = favorites.get(position);
        String[] parts = key.split("\\|", 2);
        String factText = parts.length > 1 ? parts[1] : parts[0];
        holder.textFact.setText(factText);

        holder.btnLike.setImageResource(R.drawable.baseline_favorite_24);

        holder.btnLike.setOnClickListener(v -> {
            Set<String> saved = new HashSet<>(prefs.getStringSet("fav_set", new HashSet<>()));
            if (saved.contains(key)) {
                saved.remove(key);
                prefs.edit().putStringSet("fav_set", saved).apply();
                customToast.showToast(v.getContext(), "removed from favorites",Toast.LENGTH_SHORT);
                favorites.remove(position);
                notifyItemRemoved(position);
            }
        });

        holder.btnShare.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, factText);
            context.startActivity(Intent.createChooser(share, "Share fact via"));
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView textFact;
        ImageButton btnLike, btnShare;
        VH(@NonNull View itemView) {
            super(itemView);
            textFact = itemView.findViewById(R.id.favoriteTextFact);
            btnLike = itemView.findViewById(R.id.favoriteBtnLike);
            btnShare = itemView.findViewById(R.id.favoriteBtnShare);
        }
    }
}
