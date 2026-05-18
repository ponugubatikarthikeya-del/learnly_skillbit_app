package com.factifyft.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import com.factifyft.app.R;
import com.factifyft.app.utils.AdManager;
import com.factifyft.app.utils.GameView;
import com.google.android.material.button.MaterialButton;

public class snakeGameActivity extends BaseActivity {

    private GameView gameView;
    private TextView scoreText;
    private MaterialButton btnStart;
    private boolean isGameStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake_game_show);

        scoreText = findViewById(R.id.scoreText);
        btnStart = findViewById(R.id.btnStart);
        ImageButton btnUp = findViewById(R.id.btnUp);
        ImageButton btnDown = findViewById(R.id.btnDown);
        ImageButton btnLeft = findViewById(R.id.btnLeft);
        ImageButton btnRight = findViewById(R.id.btnRight);
        ImageButton btnBack = findViewById(R.id.btnSnakeGmBack);
        gameView = findViewById(R.id.gameView);

        AdManager.initBannerAd(findViewById(R.id.ad_container));

        if (scoreText != null) {
            scoreText.setText(getString(R.string.score_0));
        }

        if (btnStart != null) {
            btnStart.setOnClickListener(v -> {
                isGameStarted = true;
                btnStart.setVisibility(View.GONE);
                if (gameView != null) {
                    gameView.resetGame();
                    gameView.resume();
                }
            });
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> AdManager.showInterstitialAd(this, this::finish));
        }

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AdManager.showInterstitialAd(snakeGameActivity.this, snakeGameActivity.this::finish);
            }
        });

        // Directional controls
        if (btnUp != null) btnUp.setOnClickListener(v -> changeDir(GameView.Direction.UP));
        if (btnDown != null) btnDown.setOnClickListener(v -> changeDir(GameView.Direction.DOWN));
        if (btnLeft != null) btnLeft.setOnClickListener(v -> changeDir(GameView.Direction.LEFT));
        if (btnRight != null) btnRight.setOnClickListener(v -> changeDir(GameView.Direction.RIGHT));

        // Listen for score updates
        if (gameView != null) {
            gameView.setOnScoreChangeListener(score ->
                    runOnUiThread(() -> {
                        if (scoreText != null) {
                            scoreText.setText(getString(R.string.score_view, score));
                        }
                    })
            );
        }
    }

    private void changeDir(GameView.Direction dir) {
        if (gameView != null) {
            gameView.changeDirection(dir);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the game was already started, just resume it. 
        // If not, show the start button.
        if (isGameStarted) {
            if (btnStart != null) btnStart.setVisibility(View.GONE);
            if (gameView != null) gameView.resume();
        } else {
            if (btnStart != null) btnStart.setVisibility(View.VISIBLE);
            if (scoreText != null) scoreText.setText(getString(R.string.score_0));
        }
    }
}
