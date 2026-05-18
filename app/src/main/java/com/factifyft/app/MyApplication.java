package com.factifyft.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.factifyft.app.activities.LoadingActivity;
import com.factifyft.app.activities.SplashScreenActivity;
import com.factifyft.app.utils.AdManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    private long lastAdShownTime = 0;
    private static final long MIN_AD_INTERVAL = 30 * 1000; // 60 sec cooldown

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        
        // Initialize AdManager (Interstitial, Rewarded, Banner setup)
        AdManager.init(this);
        
        appOpenAdManager = new AppOpenAdManager();
        appOpenAdManager.loadAd();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        showAdIfAllowed();
    }

    private void showAdIfAllowed() {
        // 1. Don't show if there's no activity
        if (currentActivity == null) return;

        // 2. Don't show ads on Splash or Loading screens (Policy & UX)
        if (currentActivity instanceof SplashScreenActivity || 
            currentActivity instanceof LoadingActivity) {
            return;
        }

        // 3. Show the ad
        appOpenAdManager.showAdIfAvailable(currentActivity);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (currentActivity == activity) {
            currentActivity = null;
        }
    }

    /**
     * Inner class to manage App Open Ad logic
     */
    private class AppOpenAdManager {
        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;
        private long loadTime = 0;

        public AppOpenAdManager() {}

        private void loadAd() {
            if (isLoadingAd || isAdAvailable()) return;
            isLoadingAd = true;
            
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(MyApplication.this, com.factifyft.app.BuildConfig.AD_UNIT_ID, request,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AppOpenAd ad) {
                            appOpenAd = ad;
                            isLoadingAd = false;
                            loadTime = (new Date()).getTime();
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            isLoadingAd = false;
                        }
                    });
        }

        private boolean wasLoadTimeLessThanNHoursAgo() {
            long dateDifference = (new Date()).getTime() - loadTime;
            long numMilliSecondsPerHour = 3600000;
            return (dateDifference < (numMilliSecondsPerHour * (long) 4));
        }

        private boolean isAdAvailable() {
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo();
        }

        public void showAdIfAvailable(@NonNull final Activity activity) {
            if (isShowingAd) return;

            if (!isAdAvailable()) {
                loadAd();
                return;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAdShownTime < MIN_AD_INTERVAL) {
                return;
            }

            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    appOpenAd = null;
                    isShowingAd = false;
                    lastAdShownTime = System.currentTimeMillis();
                    loadAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    appOpenAd = null;
                    isShowingAd = false;
                    loadAd();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    isShowingAd = true;
                }
            });

            if (!activity.isFinishing() && !activity.isDestroyed()) {
                appOpenAd.show(activity);
            }
        }
    }
}