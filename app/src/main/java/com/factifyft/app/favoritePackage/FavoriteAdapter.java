package com.factifyft.app.favoritePackage;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.factifyft.app.R;
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
        prefs = context.getSharedPreferences(context.getString(R.string.prefs_facts), Context.MODE_PRIVATE);

    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String key = favorites.get(position);
        String[] parts = key.split("\\|", 2);
        String factText = (parts.length > 1 && parts[1] != null) ? parts[1] : parts[0];
        holder.textFact.setText(factText);

        holder.btnLike.setImageResource(R.drawable.baseline_favorite_24);

        holder.btnLike.setOnClickListener(v -> {
            Set<String> saved = new HashSet<>(
                    prefs.getStringSet(context.getString(R.string.prefs_fav_set), new HashSet<>())
            );
//            Set<String> saved = new HashSet<>(prefs.getStringSet("fav_set", new HashSet<>()));
            if (saved.contains(key)) {
                saved.remove(key);
                prefs.edit().putStringSet(context.getString(R.string.prefs_fav_set), new HashSet<>(saved)).apply();
                if (position < favorites.size()) {
                    favorites.remove(position);
                }
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());

            }
        });

        holder.btnShare.setOnClickListener(v -> {
            long now=System.currentTimeMillis();
            if(now- holder.lastShareClick<1000){
                return;
            }
            holder.lastShareClick=now;
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, factText);
            context.startActivity(Intent.createChooser(share, context.getString(R.string.share_fact_via)));
        });

        holder.btnFavCopy.setOnClickListener(v -> {

            long now=System.currentTimeMillis();
            if(now- holder.lastCopyClick<1000){
                return;
            }
            holder.lastCopyClick=now;
            //get the factText for fact
            String factToCopy = holder.textFact.getText().toString();

            //get clipBoard manager from fragments's context
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

            //create a clipData with the fact
            ClipData clip = ClipData.newPlainText(context.getString(R.string.clipboard_label_fact), factToCopy);
//            ClipData clip = ClipData.newPlainText("fact", factToCopy);
            //set the clip  with the copied text
            if(clipboard != null){
            clipboard.setPrimaryClip(clip);}
//            customToast.showToast(context,"Copied", Toast.LENGTH_SHORT);
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

   public static class VH extends RecyclerView.ViewHolder {
        final  TextView textFact;
        final ImageButton btnLike, btnShare,btnFavCopy;
        VH(@NonNull View itemView) {
            super(itemView);
            textFact = itemView.findViewById(R.id.favoriteTextFact);
            btnLike = itemView.findViewById(R.id.favoriteBtnLike);
            btnShare = itemView.findViewById(R.id.favoriteBtnShare);
            btnFavCopy = itemView.findViewById(R.id.favoriteCopy);
        }
        long lastShareClick=0;
        long lastCopyClick=0;
    }
}
