package com.factifyft.app.activities;

import com.factifyft.app.utils.AdManager;
import android.animation.ObjectAnimator;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicComVsManViewActivity extends BaseActivity {

    private boolean gameActive = true;
    private final Button[] buttons = new Button[9];
    private boolean playerX = true;
    private int moveCount = 0;
    private static final int COMPUTER_DELAY = 600;

    private TextView statusText;
    private final int[][] winPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private GridLayout grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_toc_toe_game_screen);

        statusText = findViewById(R.id.statusText);
        TextView titleText = findViewById(R.id.titleText);
        grid = findViewById(R.id.gridLayout);
        Button backButton = findViewById(R.id.ticBackButton);

        AdManager.initBannerAd(findViewById(R.id.ad_container));

        // Entrance Animations
        titleText.setAlpha(0f);
        titleText.setTranslationY(-100f);
        titleText.animate().alpha(1f).translationY(0).setStartDelay(200).setDuration(800).start();

        statusText.setAlpha(0f);
        statusText.setTranslationY(100f);
        statusText.animate().alpha(1f).translationY(0).setStartDelay(400).setDuration(700).start();

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AdManager.showInterstitialAd(TicComVsManViewActivity.this, TicComVsManViewActivity.this::finish);
            }
        });

        backButton.setOnClickListener(v -> v.animate().rotationBy(180f).setDuration(300).withEndAction(() -> {
            AdManager.showInterstitialAd(this, () -> {
                Intent intent_b = new Intent(this, TicGameTypeSelectActivity.class);
                intent_b.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_b);
                finish();
            });
        }).start());
        setButtonPressEffect(backButton);

        for (int i = 0; i < 9; i++) {
            buttons[i] = (Button) grid.getChildAt(i);
            final int index = i;
            buttons[i].setOnClickListener(v -> onCellClicked(index));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setButtonPressEffect(Button button) {
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
        if (!playerX || !buttons[index].getText().toString().isEmpty() || !gameActive) return;

        buttons[index].setText("X");
        animateMark(buttons[index]);
        moveCount++;

        if (checkWin()) {
            celebrate("X");
            return;
        }

        if (moveCount == 9) {
            celebrate("DRAW");
            return;
        }

        playerX = false;
        statusText.setText(getString(R.string.computer_turn));
        disableButtons();
        handler.postDelayed(this::computerMove, COMPUTER_DELAY);
    }

    private void computerMove() {
        if (!gameActive) return;

        int move = getSmartMove();
        if (move == -1) return;

        buttons[move].setText("O");
        animateMark(buttons[move]);
        moveCount++;

        if (checkWin()) {
            celebrate("O");
            return;
        }

        if (moveCount == 9) {
            celebrate("DRAW");
            return;
        }

        playerX = true;
        statusText.setText(getString(R.string.your_turn));
        enableButtons();
    }

    private int getSmartMove() {
        // 1. Try to win
        int winMove = findWinningMove("O");
        if (winMove != -1) return winMove;

        // 2. Block player
        int blockMove = findWinningMove("X");
        if (blockMove != -1) return blockMove;

        // 3. Take center
        if (buttons[4].getText().toString().isEmpty()) return 4;

        // 4. Take random corner
        int[] corners = {0, 2, 6, 8};
        List<Integer> availableCorners = new ArrayList<>();
        for (int c : corners) {
            if (buttons[c].getText().toString().isEmpty()) availableCorners.add(c);
        }
        if (!availableCorners.isEmpty()) return availableCorners.get(random.nextInt(availableCorners.size()));

        // 5. Random empty cell
        List<Integer> emptyCells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().toString().isEmpty()) emptyCells.add(i);
        }
        if (!emptyCells.isEmpty()) return emptyCells.get(random.nextInt(emptyCells.size()));

        return -1;
    }

    private int findWinningMove(String mark) {
        for (int[] pos : winPositions) {
            String b1 = buttons[pos[0]].getText().toString();
            String b2 = buttons[pos[1]].getText().toString();
            String b3 = buttons[pos[2]].getText().toString();

            if (b1.equals(mark) && b2.equals(mark) && b3.isEmpty()) return pos[2];
            if (b1.equals(mark) && b3.equals(mark) && b2.isEmpty()) return pos[1];
            if (b2.equals(mark) && b3.equals(mark) && b1.isEmpty()) return pos[0];
        }
        return -1;
    }

    private void disableButtons() {
        for (Button b : buttons) b.setEnabled(false);
    }

    private void enableButtons() {
        for (Button b : buttons) {
            if (b.getText().toString().isEmpty()) b.setEnabled(true);
        }
    }

    private void animateMark(View v) {
        v.setScaleX(0f);
        v.setScaleY(0f);
        v.animate().scaleX(1f).scaleY(1f).rotationBy(360f).setDuration(400).start();
        ObjectAnimator.ofFloat(v, "alpha", 0.5f, 1f).setDuration(300).start();
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

    private void celebrate(String result) {
        gameActive = false;
        disableButtons();
        showWinDialog(result);
    }

    private void resetGame() {
        playerX = true;
        moveCount = 0;
        statusText.setText(getString(R.string.your_turn));
        gameActive = true;
        for (Button b : buttons) {
            b.setText("");
            b.setEnabled(true);
            b.setAlpha(1f);
            b.setScaleX(1f);
            b.setScaleY(1f);
            b.clearAnimation();
        }

        for (int i = 0; i < grid.getChildCount(); i++) {
            View cell = grid.getChildAt(i);
            cell.setScaleX(0f);
            cell.setScaleY(0f);
            cell.setAlpha(0f);
        }

        handler.postDelayed(() -> {
            for (int i = 0; i < grid.getChildCount(); i++) {
                grid.getChildAt(i).animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setStartDelay(i * 50L)
                        .setDuration(300)
                        .start();
            }
        }, 100);
    }

    private void showWinDialog(String winner) {
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

        if (winner.equals("DRAW")) {
            txtWinner.setText(getString(R.string.draw));
        } else if (winner.equalsIgnoreCase("X")) {
            txtWinner.setText(getString(R.string.you_win));
        } else {
            txtWinner.setText(getString(R.string.com_wins));
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

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
