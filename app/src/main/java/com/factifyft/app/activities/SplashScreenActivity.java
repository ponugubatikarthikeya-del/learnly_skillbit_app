package com.factifyft.app.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.splashscreen.SplashScreen;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Android 12+ splash
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // 🚀 Immediately move to next screen
        startActivity(new Intent(this, LoadingActivity.class));
        finish();
    }
}