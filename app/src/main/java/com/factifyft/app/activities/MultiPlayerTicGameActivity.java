package com.factifyft.app.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.factifyft.app.R;
import com.factifyft.app.utils.AdManager;

public class MultiPlayerTicGameActivity extends BaseActivity {

    private final Button[] buttons = new Button[9];
    private boolean playerX = true;
    private int moveCount = 0;
    private TextView statusText;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final int[][] winPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_toc_toe_game_screen);

        Intent intent = getIntent();
        if (intent != null) {
            player1Name = intent.getStringExtra(getString(R.string.intent_player1));
            player2Name = intent.getStringExtra(getString(R.string.intent_player2));
            if (player1Name == null || player1Name.isEmpty()) player1Name = getString(R.string.default_player1_name);
            if (player2Name == null || player2Name.isEmpty()) player2Name = getString(R.string.default_player2_name);
        }

        statusText = findViewById(R.id.statusText);
        TextView titleText = findViewById(R.id.titleText);
        GridLayout grid = findViewById(R.id.gridLayout);
        Button backButton = findViewById(R.id.ticBackButton);

        AdManager.initBannerAd(findViewById(R.id.ad_container));

        // Initial Animations
        titleText.setAlpha(0f);
        titleText.setTranslationY(-100f);
        titleText.animate().alpha(1f).translationY(0).setStartDelay(200).setDuration(800).start();

        statusText.setAlpha(0f);
        statusText.setTranslationY(100f);
        statusText.animate().alpha(1f).translationY(0).setStartDelay(400).setDuration(700).start();

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AdManager.showInterstitialAd(MultiPlayerTicGameActivity.this, MultiPlayerTicGameActivity.this::finish);
            }
        });

        for (int i = 0; i < grid.getChildCount(); i++) {
            View cell = grid.getChildAt(i);
            cell.setScaleX(0f);
            cell.setScaleY(0f);
            cell.setAlpha(0f);
            cell.animate().scaleX(1f).scaleY(1f).alpha(1f).setStartDelay(600L + (long) i * 80L).setDuration(300).start();
        }

        backButton.setOnClickListener(v -> v.animate().rotationBy(180f).setDuration(300).withEndAction(() -> {
            AdManager.showInterstitialAd(this, () -> {
                Intent bIntent = new Intent(this, TicGameTypeSelectActivity.class);
                bIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(bIntent);
                finish();
            });
        }).start());

        setButtonTouchEffect(backButton);

        for (int i = 0; i < 9; i++) {
            buttons[i] = (Button) grid.getChildAt(i);
            final int index = i;
            buttons[i].setOnClickListener(v -> onCellClicked(index));
        }

        statusText.setText(getString(R.string.player_turn, player1Name));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setButtonTouchEffect(View button) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                    return true;
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

    private void onCellClicked(int index) {
        if (!buttons[index].getText().toString().isEmpty()) return;

        disableButtons();
        String mark = playerX ? "X" : "O";
        buttons[index].setText(mark);
        animateMark(buttons[index]);
        moveCount++;

        if (checkWin()) {
            showWinDialog(playerX ? player1Name : player2Name, false);
        } else if (moveCount == 9) {
            showWinDialog(getString(R.string.draw1), true);
        } else {
            playerX = !playerX;
            statusText.setText(getString(R.string.turn_status, playerX ? player1Name : player2Name));
            handler.postDelayed(this::enableButtons, 400);
        }
    }

    private void animateMark(View v) {
        v.setScaleX(0f);
        v.setScaleY(0f);
        v.animate().scaleX(1f).scaleY(1f).rotationBy(360f).setDuration(400).start();
    }

    private boolean checkWin() {
        for (int[] pos : winPositions) {
            String a = buttons[pos[0]].getText().toString();
            String b = buttons[pos[1]].getText().toString();
            String c = buttons[pos[2]].getText().toString();
            if (!a.isEmpty() && a.equals(b) && b.equals(c)) return true;
        }
        return false;
    }

    private void showWinDialog(String winner, boolean isDraw) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_tic_game_over, findViewById(android.R.id.content), false);
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 0.9);
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.getAttributes().windowAnimations = R.style.animation;
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView txtWinner = dialogView.findViewById(R.id.ticWinnerName);
        if (isDraw) {
            txtWinner.setText(getString(R.string.draw));
        } else {
            txtWinner.setText(getString(R.string.player_win, winner));
        }

        View btnBack = dialogView.findViewById(R.id.ticDialogBack);
        View btnReset = dialogView.findViewById(R.id.ticDialogReset);

        btnBack.setOnClickListener(v -> {
            dialog.dismiss();
            AdManager.showInterstitialAd(this, this::finish);
        });

        btnReset.setOnClickListener(v -> {
            dialog.dismiss();
            AdManager.showRewardedAd(this, reward -> {}, this::resetGame);
        });

        dialog.show();
    }

    private void disableButtons() {
        for (Button b : buttons) b.setEnabled(false);
    }

    private void enableButtons() {
        for (Button b : buttons) {
            if (b.getText().toString().isEmpty()) b.setEnabled(true);
        }
    }

    private void resetGame() {
        for (Button b : buttons) {
            b.setText("");
            b.setEnabled(true);
            b.setAlpha(1f);
            b.setScaleX(1f);
            b.setScaleY(1f);
            b.clearAnimation();
        }
        playerX = true;
        moveCount = 0;
        statusText.setText(getString(R.string.player_turn, player1Name));
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
