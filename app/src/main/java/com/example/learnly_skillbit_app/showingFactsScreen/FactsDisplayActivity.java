package com.example.learnly_skillbit_app.showingFactsScreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.learnly_skillbit_app.DisplayFacts.FactShowingAdapter;
import com.example.learnly_skillbit_app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FactsDisplayActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private TextView titleText;
    private FactShowingAdapter adapter;
     private String category;
     private ImageButton factDisplayBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facts_display);


        titleText = findViewById(R.id.display_category_title);
        recycler = findViewById(R.id.FactDisplayRecycle);

        // Set up the RecyclerView in one item for each item

        DisplayMetrics displayMetrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height=displayMetrics.heightPixels;

        recycler.setLayoutManager(new LinearLayoutManager(this));
        factDisplayBack = findViewById(R.id.FactDisplayBack);
        factDisplayBack.setOnClickListener(v -> finish());
        Intent i = getIntent();
        category = Objects.requireNonNull(i.getStringExtra("category"));
        titleText.setText(category);

        // Build facts for the category (move to a central data source if you like)
        List<String> facts = getFactsForCategory(category);

        adapter = new FactShowingAdapter(this,facts,height);
        recycler.setAdapter(adapter);

        // Optionally, if you passed a specific fact to open, scroll to it:
        String openFact = i.getStringExtra("open_fact_text");
        if (openFact != null && !openFact.isEmpty()) {
            int idx = facts.indexOf(openFact);
            if (idx >= 0) recycler.scrollToPosition(idx);
        }
    }
    // Keep your facts provider here — populate lists as before
    private List<String> getFactsForCategory(String cat) {
        List<String> list = new ArrayList<>();  // create an empty arrayList (type String)
        String[] factArray=new String[0];  // create an empty StringArray
        int count = 0;
        if (cat == null) return list;
        switch (cat.toLowerCase()) {
            case "oceans":
               factArray = getResources().getStringArray(R.array.oceansFactsArray); //Assign ocean facts to empty string array
                break;
            case "space":
              factArray= getResources().getStringArray(R.array.spaceFactsArray);
                break;
            case "fitness":
               factArray= getResources().getStringArray(R.array.fitnessFactsArray);
                break;
            case "love":
                factArray= getResources().getStringArray(R.array.loveFactsArray);
                break;
            case "mind":
                factArray= getResources().getStringArray(R.array.mindFactsArray);
                break;
            case "men":
                factArray= getResources().getStringArray(R.array.menFactsArray);
                break;
            case "women":
                factArray= getResources().getStringArray(R.array.womenFactsArray);
                break;
            case "time & universe":
                factArray= getResources().getStringArray(R.array.time_and_universeFactsArray);
                break;
            case "nature wonders":
                factArray= getResources().getStringArray(R.array.nature_wondersFactsArray);
                break;
            case "mysteries":
                factArray= getResources().getStringArray(R.array.mysteriesFactsArray);
                break;
            case "animals":
                factArray= getResources().getStringArray(R.array.animalsFactsArray);
                break;
            default:
                count=1;
                break;
        }
        if(count==1){
            list.add("No Facts Available");
            return list;
        }
        list.addAll(List.of(factArray)); // add facts to the list
        Collections.shuffle(list); // shuffle the list of Facts
        return list;
    }
}
