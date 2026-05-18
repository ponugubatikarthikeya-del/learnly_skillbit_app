package com.factifyft.app.adapters;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.factifyft.app.R;
import com.factifyft.app.utils.AdManager;
import com.factifyft.app.utils.NetworkUtils;
import com.factifyft.app.utils.customToast;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FactShowingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_FACT = 0;
    public static final int TYPE_AD = 1;
    private static final int AD_INTERVAL = 6;

    private final Context ctx;
    private final List<Object> items;
    private final SharedPreferences prefs;
    private long lastShareClick = 0;
    private long lastCopyClick = 0;
    private int[][] PALETTE;
    private final List<NativeAd> nativeAds = new ArrayList<>();

    public FactShowingAdapter(Context ctx, List<String> facts) {
        this.ctx = ctx.getApplicationContext();
        this.items = new ArrayList<>();
        buildItems(facts);
        prefs = ctx.getSharedPreferences(ctx.getString(R.string.prefs_facts), Context.MODE_PRIVATE);
        initPalette(ctx);
    }

    private void buildItems(List<String> facts) {
        items.clear();
        for (int i = 0; i < facts.size(); i++) {
            if (i > 0 && i % AD_INTERVAL == 0) {
                items.add(null); // Placeholder for Ad
            }
            items.add(facts.get(i));
        }
    }

//    public void refreshFacts(List<String> facts) {
//        buildItems(facts);
//        notifyDataSetChanged();
//    }

    private void initPalette(Context ctx) {
        PALETTE = new int[][]{
                {ContextCompat.getColor(ctx, R.color.palette1_start), ContextCompat.getColor(ctx, R.color.palette1_end)},
                {ContextCompat.getColor(ctx, R.color.palette2_start), ContextCompat.getColor(ctx, R.color.palette2_end)},
                {ContextCompat.getColor(ctx, R.color.palette3_start), ContextCompat.getColor(ctx, R.color.palette3_end)},
                {ContextCompat.getColor(ctx, R.color.palette4_start), ContextCompat.getColor(ctx, R.color.palette4_end)},
                {ContextCompat.getColor(ctx, R.color.palette5_start), ContextCompat.getColor(ctx, R.color.palette5_end)},
                {ContextCompat.getColor(ctx, R.color.palette6_start), ContextCompat.getColor(ctx, R.color.palette6_end)}
        };
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? TYPE_AD : TYPE_FACT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_AD) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
            return new AdVH(v);
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.displayfacts_design, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // RESET: Ensure every recycled view starts as visible
        holder.itemView.setVisibility(View.VISIBLE);

        if (getItemViewType(position) == TYPE_AD) {
            AdVH adHolder = (AdVH) holder;
            final int currentPos = holder.getBindingAdapterPosition();

            // Hide initially until load success
            adHolder.itemView.setVisibility(View.GONE);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) adHolder.itemView.getLayoutParams();
            if (params != null) {
                params.height = 0;
                adHolder.itemView.setLayoutParams(params);
            }

            if (!NetworkUtils.isNetworkAvailable(ctx)) return;

            AdManager.loadNativeAd(ctx, new AdManager.NativeAdCallback() {
                @Override
                public void onLoaded(NativeAd nativeAd) {
                    if (adHolder.getBindingAdapterPosition() == currentPos) {
                        synchronized (nativeAds) {
                            nativeAds.add(nativeAd);
                        }
                        if (params != null) {
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            adHolder.itemView.setLayoutParams(params);
                        }
                        adHolder.itemView.setVisibility(View.VISIBLE);
                        adHolder.setNativeAd(nativeAd);
                    } else {
                        nativeAd.destroy();
                    }
                }

                @Override
                public void onFailure() {
                    adHolder.itemView.setVisibility(View.GONE);
                }
            });
            return;
        }

        // TYPE_FACT handling
        VH factHolder = (VH) holder;
        String fact = (String) items.get(position);
        if (fact == null) return;

        // RESET HEIGHT for facts (crucial if this VH was previously an Ad)
        int heightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 500, ctx.getResources().getDisplayMetrics());
        ViewGroup.LayoutParams factParams = factHolder.itemView.getLayoutParams();
        if (factParams != null) {
            factParams.height = heightInPx;
            factHolder.itemView.setLayoutParams(factParams);
        }

        factHolder.text.setText(fact);

        // UI Styling
        int index = position % PALETTE.length;
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{PALETTE[index][0], PALETTE[index][1]}
        );
        gd.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, ctx.getResources().getDisplayMetrics()));
        factHolder.factContainer.setBackground(gd);
        factHolder.text.setTextColor(Color.WHITE);

        // --- FIXED FAVORITE LOGIC ---
        String favKey = ctx.getString(R.string.prefs_fav_set);

        // Use a NEW HashSet to ensure we detect changes from other fragments
        Set<String> favorites = new HashSet<>(prefs.getStringSet(favKey, new HashSet<>()));

        // We match by the raw fact string as that is how FavoriteAdapter identifies items
        boolean isLiked = favorites.contains(fact);
        factHolder.btnLike.setIconResource(isLiked ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);

        factHolder.btnLike.setOnClickListener(v -> {
            // Re-fetch the latest set before modifying
            Set<String> currentFavs = new HashSet<>(prefs.getStringSet(favKey, new HashSet<>()));

            if (currentFavs.contains(fact)) {
                currentFavs.remove(fact);
                factHolder.btnLike.setIconResource(R.drawable.baseline_favorite_border_24);
            } else {
                currentFavs.add(fact);
                factHolder.btnLike.setIconResource(R.drawable.baseline_favorite_24);
                customToast.showToast(v.getContext(), ctx.getString(R.string.Liked_toast), Toast.LENGTH_SHORT);
            }
            // Save back to preferences
            prefs.edit().putStringSet(favKey, currentFavs).apply();
        });

        // Share & Copy
        factHolder.btnShare.setOnClickListener(v -> {
            if (System.currentTimeMillis() - lastShareClick < 1000) return;
            lastShareClick = System.currentTimeMillis();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain").putExtra(Intent.EXTRA_TEXT, fact);
            v.getContext().startActivity(Intent.createChooser(share, ctx.getString(R.string.share_fact_via)));
        });

        factHolder.btn_copy.setOnClickListener(v -> {
            if (System.currentTimeMillis() - lastCopyClick < 1000) return;
            lastCopyClick = System.currentTimeMillis();
            ClipboardManager cb = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cb != null) {
                cb.setPrimaryClip(ClipData.newPlainText("Fact", fact));
                customToast.showToast(v.getContext(), ctx.getString(R.string.copy), Toast.LENGTH_SHORT);
            }
        });
    }

    private String normalizeFact(String fact) {
        return fact.trim().replaceAll("\\s+", " ").toLowerCase(java.util.Locale.ROOT);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int getPositionOfFact(String fact) {
        if (fact == null) return -1;
        String target = normalizeFact(fact);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof String s && normalizeFact(s).equals(target)) return i;
        }
        return -1;
    }

    public void destroyAds() {
        synchronized (nativeAds) {
            for (NativeAd ad : nativeAds) if (ad != null) ad.destroy();
            nativeAds.clear();
        }
    }

    public static class VH extends RecyclerView.ViewHolder {
        public final ViewGroup factContainer;
        public final TextView text;
        public final MaterialButton btnLike, btn_copy, btnShare;

        public VH(@NonNull View itemView) {
            super(itemView);
            factContainer = itemView.findViewById(R.id.factContainer);
            text = itemView.findViewById(R.id.text_fact);
            btnLike = itemView.findViewById(R.id.btn_like);
            btn_copy = itemView.findViewById(R.id.btn_copy);
            btnShare = itemView.findViewById(R.id.btn_share);
        }
    }

    public static class AdVH extends RecyclerView.ViewHolder {
        public AdVH(@NonNull View itemView) { super(itemView); }

        public void setNativeAd(NativeAd nativeAd) {
            com.google.android.gms.ads.nativead.NativeAdView adView = itemView.findViewById(R.id.native_ad_view);
            if (adView == null && itemView instanceof com.google.android.gms.ads.nativead.NativeAdView) {
                adView = (com.google.android.gms.ads.nativead.NativeAdView) itemView;
            }
            if (adView == null) return;

            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
            adView.setMediaView(adView.findViewById(R.id.ad_media));

            if (adView.getHeadlineView() instanceof TextView tv) tv.setText(nativeAd.getHeadline());
            if (adView.getBodyView() instanceof TextView tv) tv.setText(nativeAd.getBody());
            if (adView.getCallToActionView() instanceof Button btn) btn.setText(nativeAd.getCallToAction());

            if (nativeAd.getIcon() == null) {
                if (adView.getIconView() != null) adView.getIconView().setVisibility(View.GONE);
            } else if (adView.getIconView() instanceof ImageView iv) {
                iv.setImageDrawable(nativeAd.getIcon().getDrawable());
                iv.setVisibility(View.VISIBLE);
            }
            adView.setNativeAd(nativeAd);
        }
    }
}