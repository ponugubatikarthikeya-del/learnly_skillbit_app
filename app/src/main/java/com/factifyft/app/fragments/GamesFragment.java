package com.factifyft.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.factifyft.app.R;
import com.factifyft.app.activities.QuizTypeSelectActivity;
import com.factifyft.app.activities.TicGameTypeSelectActivity;
import com.factifyft.app.activities.snakeGameActivity;
import com.factifyft.app.utils.AdManager;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.card.MaterialCardView;

public class GamesFragment extends Fragment {

    private NativeAd loadedNativeAd;
    private FrameLayout adContainer;
    private boolean isActionPending = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);

        MaterialCardView btnQuiz = view.findViewById(R.id.QuizPlay);
        MaterialCardView btnSnake = view.findViewById(R.id.snakeGamePlay);
        MaterialCardView btnTic = view.findViewById(R.id.ticTacToyPlay);
        adContainer = view.findViewById(R.id.native_ad_container);

        btnQuiz.setOnClickListener(v -> navigateTo(QuizTypeSelectActivity.class));
        btnSnake.setOnClickListener(v -> navigateTo(snakeGameActivity.class));
        btnTic.setOnClickListener(v -> navigateTo(TicGameTypeSelectActivity.class));

        loadAndShowNativeAd();

        return view;
    }

    private void navigateTo(Class<?> targetActivity) {
        if (isActionPending || getActivity() == null) return;
        isActionPending = true;

        AdManager.showInterstitialAd(requireActivity(), () -> {
            // This runs whether the ad finished OR failed to show
            isActionPending = false; // Reset here for safety
            Intent intent = new Intent(requireActivity(), targetActivity);
            startActivity(intent);
        });
    }

    private void loadAndShowNativeAd() {
        // Safety check: ensure we have a valid context
        if (getContext() == null) return;

        // Use the new NativeAdCallback to handle Success and Failure
        AdManager.loadNativeAd(requireContext(), new AdManager.NativeAdCallback() {
            @Override
            public void onLoaded(NativeAd nativeAd) {
                // Check if fragment is still attached to the UI
                if (isAdded() && getView() != null) {
                    loadedNativeAd = nativeAd;
                    displayNativeAd(nativeAd);
                } else {
                    // Fragment was closed during load, clean up
                    nativeAd.destroy();
                }
            }

            @Override
            public void onFailure() {
                // THE FIX: If ad fails (No Internet/No Fill), hide the container
                if (isAdded() && adContainer != null) {
                    adContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    private void displayNativeAd(NativeAd nativeAd) {
        if (adContainer == null) return;

        // Inflate layout using the container as the parent
        NativeAdView adView = (NativeAdView) LayoutInflater.from(requireContext())
                .inflate(R.layout.native_ad_layout, adContainer, false);

        populateNativeAdView(nativeAd, adView);

        // Clear and add
        adContainer.removeAllViews();
        adContainer.addView(adView);

        // Final visibility flip
        adContainer.setVisibility(View.VISIBLE);
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
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
        } else {
            if (adView.getIconView() instanceof ImageView iv) {
                iv.setImageDrawable(nativeAd.getIcon().getDrawable());
                iv.setVisibility(View.VISIBLE);
            }
        }

        if (nativeAd.getAdvertiser() == null) {
            if (adView.getAdvertiserView() != null) adView.getAdvertiserView().setVisibility(View.GONE);
        } else {
            if (adView.getAdvertiserView() instanceof TextView tv) {
                tv.setText(nativeAd.getAdvertiser());
                tv.setVisibility(View.VISIBLE);
            }
        }
        adView.setNativeAd(nativeAd);
    }

    @Override
    public void onResume() {
        super.onResume();
        isActionPending = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loadedNativeAd != null) {
            loadedNativeAd.destroy();
            loadedNativeAd = null;
        }
        adContainer = null;
    }
}
