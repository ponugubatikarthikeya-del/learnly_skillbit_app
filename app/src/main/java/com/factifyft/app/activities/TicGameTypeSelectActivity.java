package com.factifyft.app.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import com.factifyft.app.R;
import com.factifyft.app.utils.AdManager;

public class TicGameTypeSelectActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_game_type_select);

        Button multiplayerButton = findViewById(R.id.multiplayerButton);
        Button computerButton = findViewById(R.id.computerButton);
        ImageButton backButton = findViewById(R.id.ticSelBack);

        // Initialize Banner Ad
        AdManager.initBannerAd(findViewById(R.id.ad_container_sel));

        setButtonEffect(multiplayerButton);
        setButtonEffect(computerButton);

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        multiplayerButton.setOnClickListener(v -> AdManager.showInterstitialAd(this, () -> {
            Intent i = new Intent(this, MultiplayerNamesActivity.class);
            startActivity(i);
        }));

        computerButton.setOnClickListener(v -> AdManager.showInterstitialAd(this, () -> {
            Intent i = new Intent(this, TicComVsManViewActivity.class);
            startActivity(i);
        }));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setButtonEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                    return true; // Return true to consume and receive ACTION_UP
                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    v.performClick();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    return true;
            }
            return false;
        });
    }
}
