package com.factifyft.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.factifyft.app.R;
import com.factifyft.app.utils.AdManager;
import com.factifyft.app.utils.customToast;

public class MultiplayerNamesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_names);

        // Views
        EditText player1 = findViewById(R.id.player1Name);
        EditText player2 = findViewById(R.id.player2Name);
        Button startGameButton = findViewById(R.id.startGameButton);
        View container = findViewById(R.id.mainContainer);

        // Initialize Banner Ad
        AdManager.initBannerAd(findViewById(R.id.ad_container));

        // Intro Animations
        if (container != null) {
            container.animate()
                    .alpha(1f)
                    .translationY(0)
                    .setDuration(600)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }

        if (player1 != null) player1.animate().alpha(1f).translationY(0).setStartDelay(300).setDuration(500).start();
        if (player2 != null) player2.animate().alpha(1f).translationY(0).setStartDelay(450).setDuration(500).start();
        if (startGameButton != null) startGameButton.animate().alpha(1f).translationY(0).setStartDelay(600).setDuration(500).start();

        if (startGameButton != null) {
            startGameButton.setOnClickListener(v -> {
                assert player1 != null;
                String p1 = player1.getText().toString().trim();
                assert player2 != null;
                String p2 = player2.getText().toString().trim();

                if (p1.isEmpty() || p2.isEmpty()) {
                    customToast.showToast(this, getString(R.string.enter_both_names), Toast.LENGTH_SHORT);
                } else {
                    Intent intent = new Intent(this, MultiPlayerTicGameActivity.class);
                    intent.putExtra(getString(R.string.intent_player1), p1);
                    intent.putExtra(getString(R.string.intent_player2), p2);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish(); // Finish this to prevent user from coming back to name input during game
                }
            });
        }
    }
}
