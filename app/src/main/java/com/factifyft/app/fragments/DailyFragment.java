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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.factifyft.app.R;
import com.factifyft.app.utils.customToast;
import com.google.android.material.button.MaterialButton;

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
    private long lastShareClick = 0;
    private long lastCopyClick = 0;
    private SharedPreferences prefs;
    private MaterialButton dailyLike;
    private String currentFact;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_daily, container, false);

        View cardContainer = root.findViewById(R.id.cardContainer);
        frontView = root.findViewById(R.id.card_front);
        backView = root.findViewById(R.id.card_back);
        
        // Find views within the root or backView
        factText = backView.findViewById(R.id.factTextViewBack);
        dailyLike = backView.findViewById(R.id.dailyLike);
        MaterialButton dailyShare = backView.findViewById(R.id.dailyShare);
        MaterialButton dailyBtnCopy = backView.findViewById(R.id.DailyBtn_copy);

        // 3D distance setup
        float scale = getResources().getDisplayMetrics().density;
        if (cardContainer != null) {
            cardContainer.setCameraDistance(8000 * scale);
        }

        prefs = requireContext().getSharedPreferences(getString(R.string.prefs_facts), MODE_PRIVATE);

        loadDailyFact();

        if (factText != null) {
            factText.setText(currentFact);
        }

        updateLikeButtonIcon();

        // Initial visibility
        if (frontView != null && backView != null) {
            frontView.setVisibility(isFrontVisible ? View.VISIBLE : View.GONE);
            backView.setVisibility(isFrontVisible ? View.GONE : View.VISIBLE);
        }

        if (cardContainer != null) {
            cardContainer.setOnClickListener(v -> {
                if (isFrontVisible) flipToBack();
                else flipToFront();
            });
        }

        if (dailyLike != null) {
            dailyLike.setOnClickListener(v -> toggleLike());
        }

        if (dailyShare != null) {
            dailyShare.setOnClickListener(v -> shareFact());
        }

        if (dailyBtnCopy != null) {
            dailyBtnCopy.setOnClickListener(v -> copyFact());
        }

        return root;
    }

    private void loadDailyFact() {
        Set<String> showedFacts = new HashSet<>(prefs.getStringSet(getString(R.string.showed_facts), new HashSet<>()));
        String lastDate = prefs.getString(getString(R.string.lastDate), "");
        String today = new SimpleDateFormat(getString(R.string.dd_mm_yyyy), Locale.getDefault()).format(new Date());

        String[] facts = getResources().getStringArray(R.array.daily_facts);
        int lastFactCount = prefs.getInt(getString(R.string.lastFactCount), 0);
        if (facts.length > lastFactCount) {
            prefs.edit().putInt(getString(R.string.lastFactCount), facts.length).apply();
        }

        if (!today.equals(lastDate)) {
            List<String> unUsedFacts = new ArrayList<>();
            for (String fact : facts) {
                if (!showedFacts.contains(fact)) {
                    unUsedFacts.add(fact);
                }
            }

            if (unUsedFacts.isEmpty()) {
                currentFact = getString(R.string.facts_over);
            } else {
                currentFact = unUsedFacts.get(new Random().nextInt(unUsedFacts.size()));
                showedFacts.add(currentFact);
            }

            prefs.edit()
                    .putString(getString(R.string.lastDate), today)
                    .putString(getString(R.string.currentFact_key), currentFact)
                    .putStringSet(getString(R.string.showed_facts), showedFacts)
                    .apply();
        } else {
            currentFact = prefs.getString(getString(R.string.currentFact_key), getString(R.string.noFacts_today));
        }
    }

    private void updateLikeButtonIcon() {
        if (dailyLike == null || currentFact == null) return;
        Set<String> saved = prefs.getStringSet(getString(R.string.prefs_fav_set), new HashSet<>());
        String key = getString(R.string.daily_prefix) + normalizeFact(currentFact);
        dailyLike.setIconResource(saved.contains(key) ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);
    }

    private void toggleLike() {
        if (currentFact == null || currentFact.equals(getString(R.string.noFacts_today)) || currentFact.equals(getString(R.string.facts_over))) {
            return;
        }
        String key = getString(R.string.daily_prefix) + normalizeFact(currentFact);
        Set<String> currentFavs = new HashSet<>(prefs.getStringSet(getString(R.string.prefs_fav_set), new HashSet<>()));

        if (currentFavs.contains(key)) {
            currentFavs.remove(key);
            dailyLike.setIconResource(R.drawable.baseline_favorite_border_24);
        } else {
            currentFavs.add(key);
            dailyLike.setIconResource(R.drawable.baseline_favorite_24);
            customToast.showToast(requireContext(), getString(R.string.Liked_toast), Toast.LENGTH_SHORT);
        }
        prefs.edit().putStringSet(getString(R.string.prefs_fav_set), currentFavs).apply();
    }

    private void shareFact() {
        long now = System.currentTimeMillis();
        if (now - lastShareClick < 1000) return;
        lastShareClick = now;

        if (factText == null) return;
        String factToShare = factText.getText().toString();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, factToShare);
        startActivity(Intent.createChooser(share, getString(R.string.share_fact_via)));
    }

    private void copyFact() {
        long now = System.currentTimeMillis();
        if (now - lastCopyClick < 1000) return;
        lastCopyClick = now;

        if (factText == null) return;
        String factToCopy = factText.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(getString(R.string.clipboard_label_fact), factToCopy);
            clipboard.setPrimaryClip(clip);
            customToast.showToast(requireContext(), getString(R.string.copied_text), Toast.LENGTH_SHORT);
        }
    }

    private String normalizeFact(String fact) {
        return fact != null ? fact.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT) : "";
    }

    private void flipToBack() {
        if (frontView == null || backView == null) return;
        ObjectAnimator frontFlipOut = ObjectAnimator.ofFloat(frontView, "rotationY", 0f, 90f);
        frontFlipOut.setDuration(250);
        frontFlipOut.setInterpolator(new DecelerateInterpolator());
        
        ObjectAnimator backFlipIn = ObjectAnimator.ofFloat(backView, "rotationY", -90f, 0f);
        backFlipIn.setDuration(250);
        backFlipIn.setInterpolator(new DecelerateInterpolator());

        frontFlipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frontView.setVisibility(View.GONE);
                backView.setVisibility(View.VISIBLE);
                backView.setRotationY(-90f);
                backFlipIn.start();
                isFrontVisible = false;
                prefs.edit().putBoolean(getString(R.string.isFrontVisible), false).apply();
            }
        });

        frontFlipOut.start();
    }

    private void flipToFront() {
        if (frontView == null || backView == null) return;
        ObjectAnimator backFlipOut = ObjectAnimator.ofFloat(backView, "rotationY", 0f, 90f);
        backFlipOut.setDuration(250);
        backFlipOut.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator frontFlipIn = ObjectAnimator.ofFloat(frontView, "rotationY", -90f, 0f);
        frontFlipIn.setDuration(250);
        frontFlipIn.setInterpolator(new DecelerateInterpolator());

        backFlipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                backView.setVisibility(View.GONE);
                frontView.setVisibility(View.VISIBLE);
                frontView.setRotationY(-90f);
                frontFlipIn.start();
                isFrontVisible = true;
                prefs.edit().putBoolean(getString(R.string.isFrontVisible), true).apply();
            }
        });

        backFlipOut.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.edit().putBoolean(getString(R.string.isFrontVisible), isFrontVisible).apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (appStart) {
            isFrontVisible = true;
            appStart = false;
        } else {
            isFrontVisible = prefs.getBoolean(getString(R.string.isFrontVisible), true);
        }

        if (frontView != null && backView != null) {
            frontView.setVisibility(isFrontVisible ? View.VISIBLE : View.GONE);
            backView.setVisibility(isFrontVisible ? View.GONE : View.VISIBLE);
            frontView.setRotationY(0f);
            backView.setRotationY(0f);
        }
    }
}
