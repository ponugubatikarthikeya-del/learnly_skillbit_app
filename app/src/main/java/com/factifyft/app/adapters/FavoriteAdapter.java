package com.factifyft.app.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.factifyft.app.R;
import com.factifyft.app.utils.customToast;
import com.google.android.material.button.MaterialButton;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.VH> {

    private final Context context;
    private final List<String> favorites;
    private final SharedPreferences prefs;

    public FavoriteAdapter(Context context, List<String> favorites) {
        this.context = context.getApplicationContext();
        this.favorites = favorites;
        this.prefs = context.getSharedPreferences(context.getString(R.string.prefs_facts), Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_item_design, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String key = favorites.get(position);
        String[] parts = key.split("\\|", 2);
        String factText = (parts.length > 1 && parts[1] != null) ? parts[1] : parts[0];
        holder.textFact.setText(factText);

        holder.btnLike.setIconResource(R.drawable.baseline_favorite_24);

        holder.btnLike.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;

            // The key in this adapter is "id|fact_text"
            String keyToRemove = favorites.get(currentPos);

            Set<String> saved = new HashSet<>(
                    prefs.getStringSet(context.getString(R.string.prefs_fav_set), new HashSet<>())
            );

            if (saved.contains(keyToRemove)) {
                saved.remove(keyToRemove);
                // Use apply() for background saving to keep UI smooth
                prefs.edit().putStringSet(context.getString(R.string.prefs_fav_set), saved).apply();

                favorites.remove(currentPos);
                notifyItemRemoved(currentPos);
                // notifyItemRangeChanged is necessary to update positions of remaining items
                notifyItemRangeChanged(currentPos, favorites.size());
            }
        });


        holder.btnShare.setOnClickListener(v -> {
            if (System.currentTimeMillis() - holder.lastShareClick < 1000) return;
            holder.lastShareClick = System.currentTimeMillis();
            
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, factText);
            
            // Use Activity context from view to avoid needing NEW_TASK flag
            v.getContext().startActivity(Intent.createChooser(share, context.getString(R.string.share_fact_via)));
        });

        holder.btnFavCopy.setOnClickListener(v -> {
            if (System.currentTimeMillis() - holder.lastCopyClick < 1000) return;
            holder.lastCopyClick = System.currentTimeMillis();
            
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText(context.getString(R.string.clipboard_label_fact), factText);
                clipboard.setPrimaryClip(clip);
                customToast.showToast(v.getContext(), context.getString(R.string.copied_text), Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        final TextView textFact;
        final MaterialButton btnLike, btnShare, btnFavCopy;
        long lastShareClick = 0;
        long lastCopyClick = 0;

        VH(@NonNull View itemView) {
            super(itemView);
            textFact = itemView.findViewById(R.id.favoriteTextFact);
            btnLike = itemView.findViewById(R.id.favoriteBtnLike);
            btnShare = itemView.findViewById(R.id.favoriteBtnShare);
            btnFavCopy = itemView.findViewById(R.id.favoriteCopy);
        }
    }
}
