package com.factifyft.app.activities;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        // Save the current screen
        saveCurrentScreen();
    }

    private void saveCurrentScreen() {
        // DO NOT save LoadingActivity
        if (!(this instanceof LoadingActivity)) {
            SharedPreferences sp = getSharedPreferences("AppState", MODE_PRIVATE);
            sp.edit().putString("last_screen", getClass().getName()).apply();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}