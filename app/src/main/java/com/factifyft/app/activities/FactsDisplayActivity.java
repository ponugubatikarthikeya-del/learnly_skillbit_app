package com.factifyft.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.factifyft.app.R;
import com.factifyft.app.adapters.FactShowingAdapter;
import com.factifyft.app.utils.AdManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactsDisplayActivity extends AppCompatActivity {

    private FactShowingAdapter adapter;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facts_display);

        TextView titleText = findViewById(R.id.display_category_title);
        RecyclerView recycler = findViewById(R.id.FactDisplayRecycle);
        ImageButton factDisplayBack = findViewById(R.id.FactDisplayBack);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        factDisplayBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String category = intent.getStringExtra(getString(R.string.cat_category));
        if (category == null) category = getString(R.string.cat_unknown);
        titleText.setText(category);

        List<String> facts = getFactsForCategory(category);
        adapter = new FactShowingAdapter(this, facts);
        recycler.setAdapter(adapter);

        AdManager.initBannerAd(findViewById(R.id.ad_container));

        String openFact = intent.getStringExtra(getString(R.string.intent_open_fact_text));
        if (openFact != null && !openFact.isEmpty()) {
            int idx = adapter.getPositionOfFact(openFact);
            if (idx >= 0) recycler.scrollToPosition(idx);
        }

        registerNetworkRecoveryListener();
    }

    private void registerNetworkRecoveryListener() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return;

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isFinishing() || isDestroyed()) return;

                    // Only refresh ads and banner, don't clear the facts list
                    // unless it's currently showing the "No Facts" placeholder
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    AdManager.initBannerAd(findViewById(R.id.ad_container));
                });
            }
        };

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        cm.registerNetworkCallback(request, networkCallback);
    }

    private List<String> getFactsForCategory(String cat) {
        List<String> list = new ArrayList<>();
        if (cat == null) return list;

        String[] factArray;
        String catLower = cat.toLowerCase();

        // Mapping strings to arrays
        if (catLower.equalsIgnoreCase(getString(R.string.cat_oceans))) {
            factArray = getResources().getStringArray(R.array.oceansFactsArray);
        } else if (catLower.equalsIgnoreCase(getString(R.string.cat_space))) {
            factArray = getResources().getStringArray(R.array.spaceFactsArray);
        } else if (catLower.equalsIgnoreCase(getString(R.string.cat_love))) {
            factArray = getResources().getStringArray(R.array.loveFactsArray);
        } else if (catLower.equalsIgnoreCase(getString(R.string.cat_mind))) {
            factArray = getResources().getStringArray(R.array.mindFactsArray);
        } else if (catLower.equalsIgnoreCase(getString(R.string.cat_men))) {
            factArray = getResources().getStringArray(R.array.menFactsArray);
        } else if (catLower.equalsIgnoreCase(getString(R.string.cat_women))) {
            factArray = getResources().getStringArray(R.array.womenFactsArray);
        } else if (catLower.equalsIgnoreCase(getString(R.string.cat_time_universe))) {
            factArray = getResources().getStringArray(R.array.time_and_universeFactsArray);
        } else if (catLower.equalsIgnoreCase(getString(R.string.cat_nature_wonders))) {
            factArray = getResources().getStringArray(R.array.nature_wondersFactsArray);
        } else if (catLower.equalsIgnoreCase(getString(R.string.cat_mysteries))) {
            factArray = getResources().getStringArray(R.array.mysteriesFactsArray);
        } else if (catLower.equalsIgnoreCase(getString(R.string.cat_animals))) {
            factArray = getResources().getStringArray(R.array.animalsFactsArray);
        } else if (catLower.equalsIgnoreCase(getString(R.string.did_you_known))) {
            factArray = getResources().getStringArray(R.array.did_you_knownFactsArray);
        } else if (catLower.equalsIgnoreCase(getString(R.string.Human_behaviour))) {
            factArray = getResources().getStringArray(R.array.Human_behaviourFactsArray);
        } else if (catLower.equalsIgnoreCase(getString(R.string.Human_Habits))) {
            factArray = getResources().getStringArray(R.array.habit_facts);
        } else if (catLower.equalsIgnoreCase(getString(R.string.world_hidden))) {
            factArray = getResources().getStringArray(R.array.world_hidden_facts);
        } else {
            list.add(getString(R.string.no_facts_available));
            return list;
        }

        Collections.addAll(list, factArray);
//        Collections.shuffle(list);
        return list;
    }

    @Override
    protected void onDestroy() {
        if (networkCallback != null) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                try {
                    cm.unregisterNetworkCallback(networkCallback);
                } catch (Exception ignored) {}
            }
        }
        if (adapter != null) {
            adapter.destroyAds();
        }
        super.onDestroy();
    }
}