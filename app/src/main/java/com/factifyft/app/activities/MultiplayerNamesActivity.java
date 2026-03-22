package com.factifyft.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.factifyft.app.R;
import com.factifyft.app.utils.customToast;

public class MultiplayerNamesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_names);

        LinearLayout container = findViewById(R.id.mainContainer);
        EditText player1 = findViewById(R.id.player1Name);
        EditText player2 = findViewById(R.id.player2Name);
        Button startGameButton = findViewById(R.id.startGameButton);

        container.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(800)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

        player1.animate()
                .alpha(1f)
                .translationY(0)
                .setStartDelay(400)
                .setDuration(700)
                .start();

        player2.animate()
                .alpha(1f)
                .translationY(0)
                .setStartDelay(600)
                .setDuration(700)
                .start();

        startGameButton.animate()
                .alpha(1f)
                .translationY(0)
                .setStartDelay(800)
                .setDuration(700)
                .start();

        startGameButton.setOnClickListener(v -> {
            String p1 = player1.getText().toString().trim();
            String p2 = player2.getText().toString().trim();

            if (p1.isEmpty() || p2.isEmpty()) {
              customToast.showToast(this, getString(R.string.enter_both_names), Toast.LENGTH_SHORT);
            } else {
                Intent intent = new Intent(this, MultiPlayerTicGameActivity.class);
                intent.putExtra(getString(R.string.intent_player1), p1);
                intent.putExtra(getString(R.string.intent_player2), p2);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

}
