package com.example.learnly_skillbit_app.fragments;


import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnly_skillbit_app.R;
import com.example.learnly_skillbit_app.customToast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;



public class DailyFragment extends Fragment {

    private View frontView;
    private View backView;
    private boolean isFrontVisible = true;
    private TextView factText;
    private boolean appStart = true;
    private static final String PREFS_NAME = "facts_prefs";
    private static final String PREF_KEY_FAV_SET = "fav_set";
    private boolean isCopy=true;

    private SharedPreferences prefs;
    private ImageButton dailyLike;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_daily, container, false);

        View cardContainer = root.findViewById(R.id.cardContainer);
        frontView = root.findViewById(R.id.card_front);
        backView = root.findViewById(R.id.card_back);
        factText = backView.findViewById(R.id.factTextViewBack);
        dailyLike = root.findViewById(R.id.dailyLike);
        ImageButton dailyShare = root.findViewById(R.id.dailyShare);
        ImageButton DailyBtn_copy = root.findViewById(R.id.DailyBtn_copy);


        // 3D distance setup
        float scale = getResources().getDisplayMetrics().density;
        cardContainer.setCameraDistance(8000 * scale);


        prefs = requireContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        //you use this to avoid repeating facts.when selecting a new fact,you skip any that are already
        //in the set
        Set<String> showedFacts = prefs.getStringSet(PREFS_NAME, new HashSet<>());

        //Every time the app opens,you compare today's date to last date.if thy are different,it
        //mean a new day has started,so you should show a new fact
        String lastDate = prefs.getString("lastDate", "");
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        //storing facts in array (daily facts)
        String[] facts = getResources().getStringArray(R.array.daily_facts);

        //even if the user opens the app multiple times in one day,you want to show the same fact all day long.
        //so you save it and reuse it until the next day
        String currentFact = prefs.getString("currentFact", "no facts available");

        //check if new facts added when you add new facts it automatically add new facts and showing new facts
        int LastFactCount = prefs.getInt("LastFactCount", 0);   //allFacts=facts
        int currentFactCount = facts.length;
        if (currentFactCount > LastFactCount) {
            //new facts added
            prefs.edit().putInt("lastFactCount", currentFactCount).apply();
        }

        if (!today.equals(lastDate)) {
            //daily showing new Fact

            List<String> unUsedFacts = new ArrayList<>();
            for (String fact : facts) {
                if (!showedFacts.contains(fact)) {
                    unUsedFacts.add(fact);
                }
            }
            //pick to random fact from unUsedFacts
            Random random = new Random();
            if (unUsedFacts.isEmpty()) {
                currentFact = "facts are over wait a moment i will set new facts for you";
            } else {
                currentFact = unUsedFacts.get(random.nextInt(unUsedFacts.size()));
            }

            //save to prefs
            showedFacts.add(currentFact);
            prefs.edit()
                    .putString("lastDate", today)
                    .putString("currentFact", currentFact)
                    .putStringSet(PREFS_NAME, showedFacts)
                    .apply();
        }else{
            currentFact=prefs.getString("currentFact","no facts available in this day");
        }
        //show Fact text
        factText.setText(currentFact);
        Set<String> saved= prefs.getStringSet(PREF_KEY_FAV_SET, new HashSet<>());

         //check favorites status
        String key = "daily|" + normalizeFact(currentFact);
        if (saved.contains(key)) { //if you can get error then see this place
            dailyLike.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            dailyLike.setImageResource(R.drawable.baseline_favorite_border_24);
        }

        //show front or back view
        if (isFrontVisible) {
            frontView.setVisibility(View.VISIBLE);
            backView.setVisibility(View.INVISIBLE);
        } else {
            frontView.setVisibility(View.INVISIBLE);
            backView.setVisibility(View.VISIBLE);
        }

        // click listener to flip
        cardContainer.setOnClickListener(v -> {
            if (isFrontVisible) flipToBack();
            else flipToFront();
        });
        //like button logic
        dailyLike.setOnClickListener(v -> {
            // load existing set
            if (saved.contains(key)) {
                // remove
                saved.remove(key);
                dailyLike.setImageResource(R.drawable.baseline_favorite_border_24); // outline icon

            } else {
                // add
                saved.add(key);
                dailyLike.setImageResource(R.drawable.baseline_favorite_24); // filled icon

            }
            prefs.edit().putStringSet("PREF_KEY_FAV_SET", saved).apply();
            // No broadcast needed — FavoriteFragment listens to SharedPreferences changes.
        });

        //share button logic
        dailyShare.setOnClickListener(v -> {
            String factToShare = factText.getText().toString();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, factToShare);
            startActivity(Intent.createChooser(share, "Share fact via"));
        });

        DailyBtn_copy.setOnClickListener(v -> {
            //get the factText for fact
            String factToCopy = factText.getText().toString();

            //get clipBoard manager from fragments's context
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);

            //create a clipData with the fact
            ClipData clip = ClipData.newPlainText("fact", factToCopy);
            //set the clip  with the copied text
            clipboard.setPrimaryClip(clip);
        });

        return root;
    }

    private String normalizeFact(String fact){
        return fact.trim().replaceAll("\\s+", " ");
    }
    private void flipToBack() {
        ObjectAnimator frontFlipOut = ObjectAnimator.ofFloat(frontView, "rotationY", 0f, 90f);
        frontFlipOut.setDuration(200);
        frontFlipOut.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator backFlipIn = ObjectAnimator.ofFloat(backView, "rotationY", -90f, 0f);
        backFlipIn.setDuration(200);
        backFlipIn.setInterpolator(new DecelerateInterpolator());

        frontFlipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frontView.setVisibility(View.GONE);
                backView.setVisibility(View.VISIBLE);
                backView.setRotationY(-90f);
                backFlipIn.start();
                prefs.edit().putBoolean("isFrontVisible", false).apply();
            }
        });

        frontFlipOut.start();
        isFrontVisible = false;
    }

    private void flipToFront() {
        ObjectAnimator backFlipOut = ObjectAnimator.ofFloat(backView, "rotationY", 0f, 90f);
        backFlipOut.setDuration(200);
        backFlipOut.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator frontFlipIn = ObjectAnimator.ofFloat(frontView, "rotationY", -90f, 0f);
        frontFlipIn.setDuration(200);
        frontFlipIn.setInterpolator(new DecelerateInterpolator());

        backFlipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                backView.setVisibility(View.GONE);
                frontView.setVisibility(View.VISIBLE);
                frontView.setRotationY(-90f);
                frontFlipIn.start();
                prefs.edit().putBoolean("isFrontVisible", true).apply();
            }
        });

        backFlipOut.start();
        isFrontVisible = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.edit().putBoolean("isFrontVisible", isFrontVisible).apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (appStart) {
            //Always show front view when app starts
            frontView.setVisibility(View.VISIBLE);
            backView.setVisibility(View.INVISIBLE);
            isFrontVisible = true;
            prefs.edit().putBoolean("isFrontVisible", true).apply();
            appStart = false; //reset flag after resume

        } else {
            //restore the last visible side when navigating between fragments
            boolean currentPosition = prefs.getBoolean("isFrontVisible", true);//default front view
            isFrontVisible = currentPosition;
            if (isFrontVisible) {
                frontView.setVisibility(View.VISIBLE);
                backView.setVisibility(View.INVISIBLE);
            } else {
                frontView.setVisibility(View.INVISIBLE);
                backView.setVisibility(View.VISIBLE);
            }
        }

    }
}