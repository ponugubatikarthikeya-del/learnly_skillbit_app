package com.example.learnly_skillbit_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class snakeGameActivity extends AppCompatActivity {

    private GameView gameView;
    private TextView scoreText;
    private MaterialButton btnStart;
    private ImageButton btnUp, btnDown, btnLeft, btnRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snake_game_activity);
        // Bind views
        scoreText = findViewById(R.id.scoreText);
        btnStart = findViewById(R.id.btnstart);
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        gameView = findViewById(R.id.gameView);

        // Set initial score text (GameView will notify on change)
//        btnStart.setVisibility(View.VISIBLE);
//        gameView.setVisibility(View.GONE);
        scoreText.setText("Score: 0");


        btnStart.setOnClickListener(v -> {
            btnStart.setVisibility(View.GONE);  // hide button
            gameView.resume();                  // start the game loop
        });


        // Restart action

        // Directional controls
        btnUp.setOnClickListener(v -> gameView.changeDirection(GameView.Direction.UP));
        btnDown.setOnClickListener(v -> gameView.changeDirection(GameView.Direction.DOWN));
        btnLeft.setOnClickListener(v -> gameView.changeDirection(GameView.Direction.LEFT));
        btnRight.setOnClickListener(v -> gameView.changeDirection(GameView.Direction.RIGHT));

        // Listen for score updates from GameView
        gameView.setOnScoreChangeListener(score -> runOnUiThread(() -> scoreText.setText("Score: " + score)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause(); // pause game loop
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnStart.setVisibility(View.VISIBLE);
    }
}
