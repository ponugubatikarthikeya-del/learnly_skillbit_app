package com.factifyft.app.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdManager {
    private static final String TAG = "AdManager";
    
    // Frequency Capping Constants
    private static final long INTERSTITIAL_COOLDOWN = 60000; // 60 sec
    private static final long REWARDED_COOLDOWN = 30000;     // 30 sec

    private static long lastInterstitialTime = 0;
    private static long lastRewardedTime = 0;

    // IDs from BuildConfig
    private static final String INTERSTITIAL_ID = com.factifyft.app.BuildConfig.INTERSTITIAL_ID;
    private static final String REWARDED_ID = com.factifyft.app.BuildConfig.REWARDED_ID;
    private static final String BANNER_ID = com.factifyft.app.BuildConfig.BANNER_ID;
    private static final String NATIVE_ID = com.factifyft.app.BuildConfig.NATIVE_ID;

    private static InterstitialAd mInterstitialAd;
    private static RewardedAd mRewardedAd;
    private static boolean isRewardedAdLoading = false;

    public interface AdFinishedListener {
        void onAdFinished();
    }

    // ADD THIS INTERFACE to handle both success and failure in Fragments
    public interface NativeAdCallback {
        void onLoaded(NativeAd nativeAd);
        void onFailure();
    }

    public static void init(Context context) {
        Context appContext = context.getApplicationContext();
        MobileAds.initialize(appContext, initializationStatus -> {});
        loadInterstitialAd(appContext);
        loadRewardedAd(appContext);
    }

    public static void loadInterstitialAd(Context context) {
        Context appContext = context.getApplicationContext();
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(appContext, INTERSTITIAL_ID, adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mInterstitialAd = null;
                }
            });
    }

    public static void showInterstitialAd(Activity activity, AdFinishedListener listener) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            if (listener != null) listener.onAdFinished();
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (mInterstitialAd != null && (currentTime - lastInterstitialTime > INTERSTITIAL_COOLDOWN)) {
            InterstitialAd ad = mInterstitialAd;
            mInterstitialAd = null; // Clear immediately

            ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    lastInterstitialTime = System.currentTimeMillis();
                    loadInterstitialAd(activity);
                    if (listener != null) listener.onAdFinished();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    loadInterstitialAd(activity);
                    if (listener != null) listener.onAdFinished();
                }
            });

            ad.show(activity);
        } else {
            // No ad available or cooldown active
            if (mInterstitialAd == null) {
                loadInterstitialAd(activity);
            }
            if (listener != null) listener.onAdFinished();
        }
    }

    public static void loadRewardedAd(Context context) {
        if (isRewardedAdLoading || mRewardedAd != null) return;
        
        isRewardedAdLoading = true;
        Context appContext = context.getApplicationContext();
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(appContext, REWARDED_ID, adRequest,
            new RewardedAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    mRewardedAd = rewardedAd;
                    isRewardedAdLoading = false;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mRewardedAd = null;
                    isRewardedAdLoading = false;
                }
            });
    }

    public static boolean isRewardedAdLoading() {
        return isRewardedAdLoading;
    }

    public static boolean isRewardedAdLoaded() {
        return mRewardedAd != null;
    }

    public static long getRemainingRewardedCooldown() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastRewardedTime;
        if (elapsed < REWARDED_COOLDOWN) {
            return REWARDED_COOLDOWN - elapsed;
        }
        return 0;
    }

    public static boolean isRewardedAdReadyToShow() {
        return isRewardedAdLoaded() && getRemainingRewardedCooldown() <= 0;
    }

    public static void showRewardedAd(Activity activity, OnUserEarnedRewardListener rewardListener, AdFinishedListener finishedListener) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            if (finishedListener != null) finishedListener.onAdFinished();
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRewardedTime < REWARDED_COOLDOWN) {
            if (finishedListener != null) finishedListener.onAdFinished();
            return;
        }

        if (mRewardedAd != null) {
            RewardedAd ad = mRewardedAd;
            mRewardedAd = null; // Clear immediately

            ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    lastRewardedTime = System.currentTimeMillis();
                    loadRewardedAd(activity);
                    if (finishedListener != null) finishedListener.onAdFinished();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    loadRewardedAd(activity);
                    if (finishedListener != null) finishedListener.onAdFinished();
                }
            });

            ad.show(activity, rewardListener);
        } else {
            loadRewardedAd(activity);
            if (finishedListener != null) finishedListener.onAdFinished();
        }
    }

    // UPDATED: Banner Ad with auto-hide logic
    public static void initBannerAd(ViewGroup adContainer) {
        if (adContainer == null) return;
        Context context = adContainer.getContext();

        // Hide by default until loaded to prevent "empty black box"
        adContainer.setVisibility(View.GONE);
        adContainer.removeAllViews();

        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(BANNER_ID);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // ONLY show the container when the ad is actually ready
                adContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                // If it fails (like No Internet), keep it GONE
                adContainer.setVisibility(View.GONE);
                Log.e(TAG, "Banner failed: " + adError.getMessage());
            }
        });

        adContainer.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

    // UPDATED: Centralized Native Ad Loader
    public static void loadNativeAd(Context context, NativeAdCallback callback) {
        AdLoader adLoader = new AdLoader.Builder(context.getApplicationContext(), NATIVE_ID)
                .forNativeAd(nativeAd -> {
                    if (callback != null) callback.onLoaded(nativeAd);
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        Log.e(TAG, "Native ad failed: " + adError.getMessage());
                        // Trigger the failure callback so the Fragment can BE GONE the container
                        if (callback != null) callback.onFailure();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

//    public interface OnNativeAdLoadedListener {
//        void onNativeAdLoaded(NativeAd nativeAd);
//    }
}
