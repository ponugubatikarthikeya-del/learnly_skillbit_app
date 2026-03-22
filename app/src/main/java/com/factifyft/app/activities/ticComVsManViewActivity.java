package com.factifyft.app.activities;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.factifyft.app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ticComVsManViewActivity extends AppCompatActivity {

    private boolean gameActive=true;
    final Button[] buttons = new Button[9];
    boolean playerX = true;
    int moveCount = 0;
    private static final int COMPUTER_DELAY = 800;

    TextView statusText, winnerText;
    FrameLayout celebrationView;
    final int[][] winPositions = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
    };
    final Handler handler = new Handler();
    final Random random = new Random();

    private GridLayout grid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_toc_toe_game_screen);

        statusText = findViewById(R.id.statusText);
        TextView titleText = findViewById(R.id.titleText);
        winnerText = findViewById(R.id.winnerText);
        celebrationView = findViewById(R.id.celebrationView);
        grid = findViewById(R.id.gridLayout);
//        Button resetButton = findViewById(R.id.ticResetButton);
        Button backButton = findViewById(R.id.ticBackButton);

        titleText.setTranslationY(-100f);
        titleText.animate()
                .alpha(1f)
                .translationY(0)
                .setStartDelay(200)
                .setDuration(800)
                .start();

        statusText.setTranslationY(100f);
        statusText.animate()
                .alpha(1f)
                .translationY(0)
                .setStartDelay(400)
                .setDuration(700)
                .start();

        backButton.setOnClickListener(v -> {
            v.animate().rotationBy(180f).setDuration(300).start();

            findViewById(android.R.id.content).animate()
                    .translationX(-800f)
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction(() -> {
                        Intent intent_b = new Intent(ticComVsManViewActivity.this, TicGameTypeSelectActivity.class);
                        intent_b.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_b);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    }).start();
        });
        setButtonPressEffect(backButton);
//        setButtonPressEffect(resetButton);

        for (int i = 0; i < 9; i++) {
            buttons[i] = (Button) grid.getChildAt(i);
            final int index = i;
            buttons[i].setOnClickListener(v -> onCellClicked(index));
        }

//        resetButton.setOnClickListener(v -> resetGame());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setButtonPressEffect(Button button) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                    return false; // do NOT consume, allow click

                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    v.performClick();   // REQUIRED for accessibility
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
            return;}
        checkGameOver();
        if(gameActive){
           playerX=false;
           statusText.setText(getString(R.string.computer_turn));
         disableButtons(); //disable buttons while computer turn
         handler.postDelayed(this::computerMove, COMPUTER_DELAY);
        }

    }

    private void computerMove() {
        List<Integer> emptyCells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().toString().isEmpty()) emptyCells.add(i);
        }

        if (emptyCells.isEmpty()) return;

        int move = emptyCells.get(random.nextInt(emptyCells.size()));
        buttons[move].setText("O");
        animateMark(buttons[move]);
        moveCount++;

        if (checkWin()) {
            celebrate("O");
            return;}

        checkGameOver();
        if(gameActive){
            playerX=true;
            statusText.setText(getString(R.string.your_turn));
            enableButtons();//re enable after computer moves
        }}
    private void disableButtons() {
        for (Button b : buttons) b.setEnabled(false);
    }
    private void enableButtons() {
        for (Button b : buttons) {
            if(b.getText().toString().isEmpty())
                b.setEnabled(true);

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


    private void checkGameOver(){
        if(moveCount==9){
        gameActive=false;
           celebrate("DRAW"); //update celebrate to DRAW
    }}

    private  void celebrate(String result){
        gameActive=false;
        disableAllButtons();
        showWinDialog(result); //pass result instead of winner
    }


    private void disableAllButtons() {
        for (Button b : buttons) b.setEnabled(false);
    }

    private void resetGame() {
        playerX = true;
        moveCount = 0;
        statusText.setText(getString(R.string.your_turn));
//        statusText.setText("Your Turn (X)");
        gameActive=true;
        for (Button b : buttons) {
            b.setText("");
            b.setEnabled(true);
            b.setAlpha(1f);
            b.setScaleX(1f);
            b.setScaleY(1f);
        }

        for (int i = 0; i < grid.getChildCount(); i++) {
            View cell = grid.getChildAt(i);
            cell.clearAnimation();
            cell.setScaleX(0f);
            cell.setScaleY(0f);
            cell.setAlpha(0f);
        }

        handler.postDelayed(() -> {
            for (int i = 0; i < grid.getChildCount(); i++) {
                View cell = grid.getChildAt(i);
                cell.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setStartDelay(i * 100L)
                        .setDuration(300)
                        .start();
            }
        }, 200);
    }

    private void showWinDialog(String winner) {
        View dialogView = getLayoutInflater().inflate(
                R.layout.dialog_tic_game_over,
                findViewById(android.R.id.content),
                false
        );
//        View dialogView = getLayoutInflater().inflate(R.layout.tic_game_dialogbox, null,false);

        //create dialog
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false); //outside dialog cannot touchable
        //card animation and size
        DisplayMetrics metrics=getResources().getDisplayMetrics();
        int width=(int) (metrics.widthPixels*0.925);
        int height=(int) (metrics.heightPixels*0.425);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        TextView txtWinner = dialogView.findViewById(R.id.ticWinnerName);

        if(winner.equals(getString(R.string.draw1))) { //draw
            txtWinner.setText(getString(R.string.draw));
        } else if (winner.equalsIgnoreCase("X")) {
            txtWinner.setText(getString(R.string.you_win));
        } else {
            txtWinner.setText(getString(R.string.com_wins));
        }

        View btnBack = dialogView.findViewById(R.id.ticDialogBack);
        View btnReset = dialogView.findViewById(R.id.ticDialogReset);

        // BACK BUTTON
        btnBack.setOnClickListener(v -> {
            dialog.dismiss();
            finish(); // go back to previous screen
        });

        // RESET BUTTON
        btnReset.setOnClickListener(v -> {
            dialog.dismiss();
            resetGame();
        });

        dialog.show();
    }

}
