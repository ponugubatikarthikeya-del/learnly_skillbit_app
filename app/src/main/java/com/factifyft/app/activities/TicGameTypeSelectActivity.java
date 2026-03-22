package com.factifyft.app.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.factifyft.app.R;

public class TicGameTypeSelectActivity extends AppCompatActivity {

    Button multiplayerButton, computerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_game_type_select);

        multiplayerButton = findViewById(R.id.multiplayerButton);
        computerButton = findViewById(R.id.computerButton);

        setButtonEffect(multiplayerButton);
        setButtonEffect(computerButton);

        multiplayerButton.setOnClickListener(v -> {
            Intent i = new Intent(TicGameTypeSelectActivity.this, MultiplayerNamesActivity.class);
            startActivity(i);
        });

        computerButton.setOnClickListener(v -> {
            Intent i = new Intent(TicGameTypeSelectActivity.this, ticComVsManViewActivity.class);
            startActivity(i);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setButtonEffect(Button button) {
        button.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                    return false; // allow click to continue
                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    v.performClick();   // ⭐ REQUIRED for accessibility + removes warning
                    return true;        // consume the UP event

                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    return true;
            }

            return false;
        });
    }}

