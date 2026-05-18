package com.factifyft.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import com.factifyft.app.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LoadingActivity extends BaseActivity {
    private final Handler handler = new Handler(Looper.getMainLooper());

    private int quoteIndex = 0;
    private List<String> loadQuoteArray;
    private final Handler quoteHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        quoteSet();
        // Disable back button while loading
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing to keep user on loading screen
            }
        });

        handler.postDelayed(() -> {
            if (!isFinishing() && !isDestroyed()) {
                // Always go to DashBoardActivity, regardless of internet
                startActivity(new Intent(this, DashBoardActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 3000);
    }

    private void quoteSet() {

        TextView loadQuote = findViewById(R.id.loadQuote);
        loadQuoteArray = Arrays.asList(getResources().getStringArray(R.array.loading_quotes));
        Collections.shuffle(loadQuoteArray);

        Runnable quoteRunnable = new Runnable() {
            @Override
            public void run() {
                loadQuote.animate().alpha(0f).setDuration(300).withEndAction(()->
                {
                    //change the text when invisible
                    loadQuote.setText(loadQuoteArray.get(quoteIndex));
                    quoteIndex = (quoteIndex + 1) % loadQuoteArray.size();

                    //create fade in animation
                    loadQuote.animate().alpha(1f).setDuration(300).start();
                }).start();

                //schedule next quote in 1500 ms
                quoteHandler.postDelayed(this,1500);
            }
        };

        // start the first execution
        quoteHandler.post(quoteRunnable);

    }

    @Override
    protected void onDestroy() {
        // 🛑 CRITICAL: Prevent memory leak and crashes by removing callbacks
        handler.removeCallbacksAndMessages(null);

        // for quote memory leak prevention
        quoteHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
