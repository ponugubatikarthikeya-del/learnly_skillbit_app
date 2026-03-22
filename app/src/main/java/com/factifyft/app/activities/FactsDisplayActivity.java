package com.factifyft.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.factifyft.app.adapters.FactShowingAdapter;
import com.factifyft.app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactsDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facts_display);

        TextView titleText = findViewById(R.id.display_category_title);
        RecyclerView recycler = findViewById(R.id.FactDisplayRecycle);

        // Set up the RecyclerView in one item for each item
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        if (wm != null) {
            wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        recycler.setLayoutManager(new LinearLayoutManager(this));
        ImageButton factDisplayBack = findViewById(R.id.FactDisplayBack);
        factDisplayBack.setOnClickListener(v -> finish());
        Intent i = getIntent();

        String category = i.getStringExtra(getString(R.string.cat_category));
        if (category == null) category = getString(R.string.cat_unknown);
        titleText.setText(category);

        // Build facts for the category (move to a central data source if you like)
        List<String> facts = getFactsForCategory(category);
        FactShowingAdapter adapter = new FactShowingAdapter(this, facts);
        recycler.setAdapter(adapter);
        // Optionally, if you passed a specific fact to open, scroll to it:
        String openFact = i.getStringExtra(getString(R.string.intent_open_fact_text));
//        String openFact = i.getStringExtra("open_fact_text");
        if (openFact != null && !openFact.isEmpty()) {
            int idx = facts.indexOf(openFact);
            if (idx >= 0) recycler.scrollToPosition(idx);
        }
    }


    private List<String> getFactsForCategory(String cat) {
        List<String> list = new ArrayList<>();
        if (cat == null) return list;

        String[] factArray;
        String catLower = cat.toLowerCase();

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

        } else {
            list.add(getString(R.string.no_facts_available));
            return list;
        }

        list.addAll(List.of(factArray));
        Collections.shuffle(list);
        return list;
    }


}
