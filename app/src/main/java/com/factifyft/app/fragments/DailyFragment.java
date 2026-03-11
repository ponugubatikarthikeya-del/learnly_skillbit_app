package com.factifyft.app.fragments;


import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.factifyft.app.R;

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
    private long lastShareClick,lastCopyClick;
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

        prefs = requireContext().getSharedPreferences(getString(R.string.prefs_facts), MODE_PRIVATE);

//        prefs = requireContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        //you use this to avoid repeating facts.when selecting a new fact,you skip any that are already
        //in the set
        Set<String> showedFacts =new HashSet<>(prefs.getStringSet(getString(R.string.showed_facts), new HashSet<>()));

        //Every time the app opens,you compare today's date to last date.if thy are different,it
        //mean a new day has started,so you should show a new fact
        String lastDate = prefs.getString(getString(R.string.lastDate), "");
        String today = new SimpleDateFormat(getString(R.string.dd_mm_yyyy), Locale.getDefault()).format(new Date());

        //storing facts in array (daily facts)
        String[] facts = getResources().getStringArray(R.array.daily_facts);

        //even if the user opens the app multiple times in one day,you want to show the same fact all day long.
        //so you save it and reuse it until the next day
        String currentFact;

        //check if new facts added when you add new facts it automatically add new facts and showing new facts
        int LastFactCount = prefs.getInt(getString(R.string.lastFactCount), 0);   //allFacts=facts
        int currentFactCount = facts.length;
        if (currentFactCount > LastFactCount) {
            //new facts added
            prefs.edit().putInt(getString(R.string.lastFactCount), currentFactCount).apply();
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
                currentFact =getString(R.string.facts_over);
            } else {
                currentFact = unUsedFacts.get(random.nextInt(unUsedFacts.size()));
            }

            //save to prefs
            showedFacts.add(currentFact);
            prefs.edit()
                    .putString(getString(R.string.lastDate), today)
                    .putString(getString(R.string.currentFact_key), currentFact)
//                    .putString("lastDate", today)
//                    .putString("currentFact", currentFact)
                    .putStringSet(getString(R.string.showed_facts), showedFacts)
                    .apply();
        }else{
            currentFact = prefs.getString(getString(R.string.currentFact_key), getString(R.string.noFacts_today));

//            currentFact=prefs.getString("currentFact","no facts available in this day");
        }
        //show Fact text
        if(factText != null){
        factText.setText(currentFact);}

        Set<String> saved = new HashSet<>(prefs.getStringSet(getString(R.string.prefs_fav_set), new HashSet<>()));
//        Set<String> saved= new HashSet<>(prefs.getStringSet(PREF_KEY_FAV_SET, new HashSet<>()));

         //check favorites status
        String key = getString(R.string.daily_prefix) + normalizeFact(currentFact);
//        String key = "daily|" + normalizeFact(currentFact);
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
            prefs.edit().putStringSet(getString(R.string.prefs_fav_set), new HashSet<>(saved)).apply();
//            prefs.edit().putStringSet(PREF_KEY_FAV_SET,new HashSet<>(saved)).apply();
            // No broadcast needed — FavoriteFragment listens to SharedPreferences changes.
        });

        //share button logic
       lastShareClick=0;
       lastCopyClick=0;
        dailyShare.setOnClickListener(v -> {
                long now=System.currentTimeMillis();
                if(now-lastShareClick<1000){
                    return;
                }
                lastShareClick=now;
            String factToShare = factText.getText().toString();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, factToShare);
            startActivity(Intent.createChooser(share, getString(R.string.share_fact_via)));
//            startActivity(Intent.createChooser(share, "Share fact via"));
        });

        DailyBtn_copy.setOnClickListener(v -> {
            long now=System.currentTimeMillis();
            if(now-lastCopyClick<1000) {
                return;
            }
            lastCopyClick=now;
            //get the factText for fact
            String factToCopy = factText.getText().toString();

            //get clipBoard manager from fragments's context
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);

            //create a clipData with the fact
            ClipData clip = ClipData.newPlainText(getString(R.string.clipboard_label_fact), factToCopy);
//            ClipData clip = ClipData.newPlainText("fact", factToCopy);
            //set the clip  with the copied text
            if(clipboard != null){
            clipboard.setPrimaryClip(clip);}
//            customToast.showToast(requireContext(),"Copied",Toast.LENGTH_SHORT);
        });

        return root;
    }

    private String normalizeFact(String fact){
        return fact.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
    private void flipToBack() {
        if(frontView == null || backView == null) return;
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
                prefs.edit().putBoolean(getString(R.string.isFrontVisible), false).apply();
            }
        });

        frontFlipOut.start();
        isFrontVisible = false;
    }

    private void flipToFront() {
        if(frontView == null || backView == null) return;
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
                prefs.edit().putBoolean(getString(R.string.isFrontVisible), true).apply();
            }
        });

        backFlipOut.start();
        isFrontVisible = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.edit().putBoolean(getString(R.string.isFrontVisible), isFrontVisible).apply();
    }
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = requireContext().getSharedPreferences(getString(R.string.prefs_facts), Context.MODE_PRIVATE);
//        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (appStart) {
            //Always show front view when app starts
            frontView.setVisibility(View.VISIBLE);
            backView.setVisibility(View.INVISIBLE);
            isFrontVisible = true;
            prefs.edit().putBoolean(getString(R.string.isFrontVisible), true).apply();
            appStart = false; //reset flag after resume
        } else {
            //restore the last visible side when navigating between fragments
            isFrontVisible = prefs.getBoolean(getString(R.string.isFrontVisible), true);
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