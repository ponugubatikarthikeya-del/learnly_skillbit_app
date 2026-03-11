package com.factifyft.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class snakeGameActivity extends AppCompatActivity {

    private GameView gameView;
    private TextView scoreText;
    private MaterialButton btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snake_game_activity);
        // Bind views
        scoreText = findViewById(R.id.scoreText);
        btnStart = findViewById(R.id.btnStart);
        ImageButton btnUp = findViewById(R.id.btnUp);
        ImageButton btnDown = findViewById(R.id.btnDown);
        ImageButton btnLeft = findViewById(R.id.btnLeft);
        ImageButton btnRight = findViewById(R.id.btnRight);
        gameView = findViewById(R.id.gameView);

        scoreText.setText(getString(R.string.score_0));

        btnStart.setOnClickListener(v -> {
            btnStart.setVisibility(View.GONE);  // hide button
            gameView.resetGame();              // restart the game
            gameView.resume();                  // start the game loop
        });


        // Restart action

        // Directional controls
        btnUp.setOnClickListener(v -> {
            if (gameView != null) {
                gameView.changeDirection(GameView.Direction.UP);
            }
        });

        btnDown.setOnClickListener(v -> {
            if (gameView != null) {
                gameView.changeDirection(GameView.Direction.DOWN);
            }
        });

        btnLeft.setOnClickListener(v -> {
            if (gameView != null) {
                gameView.changeDirection(GameView.Direction.LEFT);
            }
        });

        btnRight.setOnClickListener(v -> {
            if (gameView != null) {
                gameView.changeDirection(GameView.Direction.RIGHT);
            }
        });

        // Listen for score updates
        if (gameView != null) {
            gameView.setOnScoreChangeListener(score ->
                    runOnUiThread(() -> scoreText.setText(getString(R.string.score_view, score)))
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(gameView != null){
        gameView.pause(); }// pause game loop
    }

    @Override
    protected void onResume() {
        super.onResume();
        //show start button again
        if(btnStart != null) {
            btnStart.setVisibility(View.VISIBLE);
        }
        if(scoreText != null)
        {
            scoreText.setText(getString(R.string.score_0));
        }
    }
}
