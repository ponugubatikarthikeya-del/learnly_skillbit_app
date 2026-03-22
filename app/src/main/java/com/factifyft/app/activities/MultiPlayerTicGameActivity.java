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
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.factifyft.app.R;

public class MultiPlayerTicGameActivity extends AppCompatActivity {

    final Button[] buttons = new Button[9];
    boolean playerX = true;
    int moveCount = 0;
    TextView statusText, winnerText;
    FrameLayout celebrationView;
    // player names
    String player1Name = "Player 1";
    String player2Name = "Player 2";
    private GridLayout grid;

    final  int[][] winPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };

   final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_toc_toe_game_screen);

        // Get names from previous screen
        Intent intent = getIntent();

        if (intent != null) {
            player1Name = intent.getStringExtra(getString(R.string.intent_player1));
            player2Name = intent.getStringExtra(getString(R.string.intent_player2));

            if (player1Name == null || player1Name.isEmpty()) {
                player1Name = getString(R.string.default_player1_name);
            }

            if (player2Name == null || player2Name.isEmpty()) {
                player2Name = getString(R.string.default_player2_name);
            }
        }
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

        for (int i = 0; i < grid.getChildCount(); i++) {
            View cell = grid.getChildAt(i);
            cell.setScaleX(0f);
            cell.setScaleY(0f);
            cell.setAlpha(0f);

            cell.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setStartDelay(600L + (long) i * 80L)
//                    .setStartDelay(600 + i * 80)
                    .setDuration(300)
                    .start();
        }

        backButton.setOnClickListener(v -> {
            v.animate().rotationBy(180f).setDuration(300).start();

            findViewById(android.R.id.content).animate()
                    .translationX(-800f)
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction(() -> {
                        Intent intent_b = new Intent(MultiPlayerTicGameActivity.this, TicGameTypeSelectActivity.class);
                        intent_b.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_b);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    }).start();
        });

        setButtonTouchEffect(backButton);


        for (int i = 0; i < 9; i++) {
            buttons[i] = (Button) grid.getChildAt(i);
            final int index = i;
            buttons[i].setOnClickListener(v -> onCellClicked(index));
        }


        statusText.setText(getString(R.string.player_turn, player1Name));
//        statusText.setText(player1Name + "'s Turn");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setButtonTouchEffect(Button button) {

        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                    return true;

                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    v.performClick();   // REQUIRED to remove the warning
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

        disableButtons(); //disable all buttons

        String mark = playerX ? "X" : "O";
        buttons[index].setText(mark);
        animateMark(buttons[index]);
        moveCount++;

        if (checkWin()) {
            celebrate(playerX ? player1Name : player2Name);
        } else if (moveCount == 9) {
            showDrawDialog();
        } else {
            playerX = !playerX;
            statusText.setText(
                    getString(R.string.turn_status, playerX ? player1Name : player2Name)
            );
//            statusText.setText((playerX ? player1Name : player2Name) + "'s Turn");
            handler.postDelayed(this::enableButtons, 500); //re-enable after animation
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

    private void celebrate(String winner) {

        showWinDialog(winner);
}

private void showDrawDialog(){
    showWinDialog(getString(R.string.draw1));
}

    private void showWinDialog(String winner) {
        View dialogView = getLayoutInflater().inflate(
                R.layout.dialog_tic_game_over,
                findViewById(android.R.id.content),
                false
        );
//        View dialogView = getLayoutInflater().inflate(R.layout.tic_game_dialogbox, null);

        //create dialog
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false); //outside dialog cannot touchable
        //card animation and size
        DisplayMetrics metrics=getResources().getDisplayMetrics();
        int width=(int) (metrics.widthPixels*0.925);
        int height=(int) (metrics.heightPixels*0.5);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(width, height);
            window.getAttributes().windowAnimations = R.style.animation;
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView txtWinner = dialogView.findViewById(R.id.ticWinnerName);

        if(winner.equals(getString(R.string.draw1))){
            txtWinner.setText(getString(R.string.draw));}
        else if(winner.equalsIgnoreCase("X")){
            txtWinner.setText(getString(R.string.player_win, player1Name));
            //            txtWinner.setText("🎉 "+ player1Name + " Win!");}
        }
        else{
            txtWinner.setText(getString(R.string.player_win, player2Name));
        }
//            txtWinner.setText("🎉 " + player2Name + " wins! ");}
//        txtWinner.setText("🎉 " + (winner.equals("X") ? "You Win!" : "Com Wins!") + " 🎉");

        View btnBack = dialogView.findViewById(R.id.ticDialogBack);
        View btnReset = dialogView.findViewById(R.id.ticDialogReset);

        // BACK BUTTON
        btnBack.setOnClickListener(v -> {
            dialog.dismiss();
           Intent intent=new Intent(MultiPlayerTicGameActivity.this, TicGameTypeSelectActivity.class);
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
           startActivity(intent);
           finish(); // go back to previous screen
        });

        // RESET BUTTON
        btnReset.setOnClickListener(v -> {
            dialog.dismiss();
            resetGame();
        });

        dialog.show();
    }

    public void disableButtons(){
        for (Button b : buttons) b.setEnabled(false);
    }
    private void enableButtons(){
        for(Button b:buttons){
            if(b.getText().toString().isEmpty())
                b.setEnabled(true);
        }
    }

    private void resetGame() {
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

        playerX = true;
        moveCount = 0;
//        statusText.setText("Your Turn (X)");
        statusText.setText(getString(R.string.player_turn, player1Name));
//        statusText.setText(player1Name + "'s Turn");
    }
}
