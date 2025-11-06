package com.example.learnly_skillbit_app.DisplayFacts;


import static androidx.core.util.TypedValueCompat.dpToPx;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnly_skillbit_app.R;
import com.example.learnly_skillbit_app.customToast;


import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class FactShowingAdapter extends RecyclerView.Adapter<FactShowingAdapter.VH> {

    private final Context ctx;
    private final List<String> facts;
   private final SharedPreferences prefs;
   private  int width,height;
    private static final String PREFS_NAME = "facts_prefs";
    private static final String PREF_KEY_FAV_SET = "fav_set";

    private final int[][] PALETTE = {
            {Color.parseColor("#00C6FF"), Color.parseColor("#0072FF")},
            {Color.parseColor("#FF6B6B"), Color.parseColor("#FFD93D")},
            {Color.parseColor("#8E2DE2"), Color.parseColor("#4A00E0")},
            {Color.parseColor("#43CEA2"), Color.parseColor("#185A9D")},
            {Color.parseColor("#FF9A8B"), Color.parseColor("#FF6A88")},
            {Color.parseColor("#4ECDC4"), Color.parseColor("#556270")}
    };


    private int getContrastingTextColor(int color1, int color2) {
        int r = (Color.red(color1) + Color.red(color2)) / 2;
        int g = (Color.green(color1) + Color.green(color2)) / 2;
        int b = (Color.blue(color1) + Color.blue(color2)) / 2;
        double luminance = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255.0;
        return luminance > 0.6 ? Color.BLACK : Color.WHITE;
    }
    public FactShowingAdapter(Context ctx,List<String> facts,int height) {
        this.ctx = ctx;
        this.facts=facts;
        this.height=height;
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.displayfacts_design, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String fact = facts.get(position);
        holder.text.setText(fact);

        //set height width of card view
        int cardHeight=dpToPx(500,ctx); //fixed height
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

//        //pick random color from list of gradients
//        Random random=new Random();
//        int[] colors= PALETTE[random.nextInt(PALETTE.length)];
//        GradientDrawable randomGradient=new GradientDrawable(
//        GradientDrawable.Orientation.TL_BR,new int[]{colors[0],colors[1]});
//        randomGradient.setCornerRadius(40f);
//        holder.text.setBackground(randomGradient);

        // Like toggle
        Set<String> saved = new HashSet<>(prefs.getStringSet(PREF_KEY_FAV_SET, new HashSet<>()));

        String key =  "fact|" + fact.trim().replaceAll("\\s+", " ");
        boolean liked = saved.contains(key);
        holder.btnLike.setImageResource(liked ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);

        // Like toggle
        holder.btnLike.setOnClickListener(v -> {
            if (saved.contains(key)) {
                saved.remove(key);
                holder.btnLike.setImageResource(R.drawable.baseline_favorite_border_24);
            } else {
                saved.add(key);
                holder.btnLike.setImageResource(R.drawable.baseline_favorite_24);
            }
            prefs.edit().putStringSet(PREF_KEY_FAV_SET, saved).apply();
            // FavoriteFragment listens to prefs and will update automatically.
        });

        // Share
        holder.btnShare.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, fact);
            ctx.startActivity(Intent.createChooser(share, "Share fact via"));
        });
        holder.btn_copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Fact", holder.text.getText().toString());
            clipboard.setPrimaryClip(clip);
        });

    }
    private int dpToPx(int dp, Context context) {
         return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()
        );
    }

    @Override
    public int getItemCount() {
        return facts.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        private LinearLayout factContainer;
        private  LinearLayout buttons;
        TextView text;
        ImageButton btnLike, btnShare,btn_copy;

        VH(@NonNull View itemView) {
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
