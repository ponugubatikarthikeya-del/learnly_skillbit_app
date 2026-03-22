package com.factifyft.app.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.factifyft.app.R;
import com.factifyft.app.utils.customToast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FactShowingAdapter extends RecyclerView.Adapter<FactShowingAdapter.VH> {

    private final Context ctx;
    private final List<String> facts;
   private final SharedPreferences prefs;
   private long lastShareClick=0;
   private long lastCopyClick=0;
    private int[][] PALETTE;

    private void initPalette(Context ctx) {
        PALETTE = new int[][]{
                {
                        ContextCompat.getColor(ctx, R.color.palette1_start),
                        ContextCompat.getColor(ctx, R.color.palette1_end)
                },
                {
                        ContextCompat.getColor(ctx, R.color.palette2_start),
                        ContextCompat.getColor(ctx, R.color.palette2_end)
                },
                {
                        ContextCompat.getColor(ctx, R.color.palette3_start),
                        ContextCompat.getColor(ctx, R.color.palette3_end)
                },
                {
                        ContextCompat.getColor(ctx, R.color.palette4_start),
                        ContextCompat.getColor(ctx, R.color.palette4_end)
                },
                {
                        ContextCompat.getColor(ctx, R.color.palette5_start),
                        ContextCompat.getColor(ctx, R.color.palette5_end)
                },
                {
                        ContextCompat.getColor(ctx, R.color.palette6_start),
                        ContextCompat.getColor(ctx, R.color.palette6_end)
                }
        };
    }

    public FactShowingAdapter(Context ctx,List<String> facts) {
        this.ctx = ctx;
        this.facts=facts;
        prefs = ctx.getSharedPreferences(ctx.getString(R.string.prefs_facts), Context.MODE_PRIVATE);
        initPalette(ctx);  // initialize safe colors
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.displayfacts_design, parent, false);
        return new VH(v);
    }
    @Override
    public boolean onFailedToRecycleView(@NonNull VH holder) {
        return true;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if(position>=facts.size() || facts.get(position)==null){
            holder.text.setText(ctx.getString(R.string.no_facts_available));
            return;
        }
        String fact = facts.get(position);
        holder.text.setText(fact);

        //set height width of card view
        int cardHeight=dpToPx(ctx); //fixed height
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.height = cardHeight;
        holder.itemView.setLayoutParams(params);

        //set background colors of card view(randomly from list of colors)
        int index = position % PALETTE.length; // consistent per position
        int startColor = PALETTE[index][0];
        int endColor = PALETTE[index][1];

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{startColor, endColor}
        );
        gradientDrawable.setCornerRadius(0f);

        holder.factContainer.setBackground(gradientDrawable);
        holder.buttons.setBackgroundResource(R.drawable.white_bottom_rounded);
        // Optional: adjust text color for readability
        int textColor = Color.BLACK;
        holder.text.setTextColor(textColor);

        // Like toggle
        Set<String> saved1 = new HashSet<>(prefs.getStringSet(ctx.getString(R.string.prefs_fav_set), new HashSet<>()));

        String key1 = ctx.getString(R.string.fact_prefix) + normalizeFact(fact);
        boolean liked = saved1.contains(key1);
        holder.btnLike.setImageResource(liked ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);

        // Like toggle
        holder.btnLike.setOnClickListener(v -> {
            String key= ctx.getString(R.string.fact_prefix) + normalizeFact(fact);

            //Always fetch the  latest  set
            Set<String> current = new HashSet<>(prefs.getStringSet(ctx.getString(R.string.prefs_fav_set), new HashSet<>()));
            Set<String> saved = new HashSet<>(current);

            if (saved.contains(key)) {
                saved.remove(key);
                holder.btnLike.setImageResource(R.drawable.baseline_favorite_border_24);
            } else {
                saved.add(key);
                holder.btnLike.setImageResource(R.drawable.baseline_favorite_24);
                customToast.showToast(ctx,ctx.getString(R.string.Liked_toast), Toast.LENGTH_SHORT);
            }
            prefs.edit().putStringSet(ctx.getString(R.string.prefs_fav_set), saved).apply();
            // FavoriteFragment listens to prefs and will update automatically.
        });

        // Share
        holder.btnShare.setOnClickListener(v -> {
            long now=System.currentTimeMillis();
            if(now-lastShareClick<1000){
                return;
            }
            lastShareClick=now;
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, fact);
            ctx.startActivity(Intent.createChooser(share, ctx.getString(R.string.share_fact_via)));
        });
        holder.btn_copy.setOnClickListener(v -> {
            long now=System.currentTimeMillis();
            if(now-lastCopyClick<1000){
                return;
            }
            lastCopyClick=now;
            ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(
                    ctx.getString(R.string.clipboard_label),
                    holder.text.getText().toString()
            );
            if(clipboard != null){
                clipboard.setPrimaryClip(clip);
            }
        });

    }

    private String normalizeFact(String fact){
        return fact.trim().replaceAll("\\s+", " ").toLowerCase();
    }
    private int dpToPx(Context context) {
         return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 500, context.getResources().getDisplayMetrics()  //height 500 changes
        );
    }

    @Override
    public int getItemCount() {
        return facts.size();
    }

   public static class VH extends RecyclerView.ViewHolder {
        public final LinearLayout factContainer;
        public final LinearLayout buttons;
        public final TextView text;
        public final ImageButton btnLike, btnShare,btn_copy;

        public VH(@NonNull View itemView) {
            super(itemView);
            factContainer= itemView.findViewById(R.id.factContainer);
            buttons= itemView.findViewById(R.id.factButtonView);
            text = itemView.findViewById(R.id.text_fact);
            btnLike = itemView.findViewById(R.id.btn_like);
            btn_copy = itemView.findViewById(R.id.btn_copy);
            btnShare = itemView.findViewById(R.id.btn_share);
        }
    }
}
