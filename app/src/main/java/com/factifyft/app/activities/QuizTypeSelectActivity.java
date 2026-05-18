package com.factifyft.app.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.factifyft.app.R;
import com.factifyft.app.utils.AdManager;
import com.factifyft.app.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class QuizTypeSelectActivity extends BaseActivity {

    private boolean isCardClickable = true;
    private boolean isRewardEarned = false;

    private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_type_select);

        AdManager.initBannerAd(findViewById(R.id.ad_container));

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        setupCard(R.id.quizPsychology, R.string.quiz_psychology, R.drawable.psycologybg);
        setupCard(R.id.quizTechnology, R.string.quiz_technology, R.drawable.quiz_technology_bg);
        setupCard(R.id.quizUniverse, R.string.quiz_universe, R.drawable.qui_universebg);
        setupCard(R.id.quizScience, R.string.quiz_science, R.drawable.quiz_sci_img);
        setupCard(R.id.quizSports, R.string.quiz_sports, R.drawable.quiz_sports_img);
        setupCard(R.id.quizKnowledge, R.string.quiz_knowledge, R.drawable.quiz_gk_img);
        
        updateLockStates();
    }

    private void setupCard(int cardId, int stringResId, int iconResId) {
        MaterialCardView card = findViewById(cardId);
        String type = getString(stringResId);
        if (card != null) {
            card.setOnClickListener(view -> {
                if (isUnlocked(type)) {
                    startQuiz(type, true); // Show interstitial for already unlocked
                } else {
                    showUnlockDialog(type, iconResId);
                }
            });
        }
    }

    private boolean isUnlocked(String type) {
        if (getString(R.string.quiz_psychology).equals(type)) return true;
        return getSharedPreferences("QuizPrefs", MODE_PRIVATE).getBoolean("unlocked_" + type, false);
    }

    private void updateLockStates() {
        setLockUI(R.id.lockPsychology, R.id.contentPsychology, getString(R.string.quiz_psychology));
        setLockUI(R.id.lockTechnology, R.id.contentTechnology, getString(R.string.quiz_technology));
        setLockUI(R.id.lockUniverse, R.id.contentUniverse, getString(R.string.quiz_universe));
        setLockUI(R.id.lockScience, R.id.contentScience, getString(R.string.quiz_science));
        setLockUI(R.id.lockSports, R.id.contentSports, getString(R.string.quiz_sports));
        setLockUI(R.id.lockKnowledge, R.id.contentKnowledge, getString(R.string.quiz_knowledge));
    }

    private void setLockUI(int lockId, int contentId, String type) {
        ImageView lock = findViewById(lockId);
        View content = findViewById(contentId);
        if (lock != null && content != null) {
            if (isUnlocked(type)) {
                lock.setVisibility(View.GONE);
                content.setAlpha(1.0f);
            } else {
                lock.setVisibility(View.VISIBLE);
                content.setAlpha(0.5f);
            }
        }
    }

    private void showUnlockDialog(String type, int iconResId) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_unlock_quiz);
        dialog.setCancelable(true);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Set dialog width to match parent with some margin
            dialog.getWindow().setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        TextView title = dialog.findViewById(R.id.dialogTitle);
        ImageView icon = dialog.findViewById(R.id.dialogIcon);
        MaterialButton btnWatchAd = dialog.findViewById(R.id.btnWatchAd);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView tvMsg = dialog.findViewById(R.id.dialogMessage);

        title.setText(getString(R.string.unlock_category, type));
        icon.setImageResource(iconResId);

        isRewardEarned = false;

        // Runnable to update message dynamically based on AdMob cooldown and Internet
        Runnable updateStatus = new Runnable() {
            @Override
            public void run() {
                if (!NetworkUtils.isNetworkAvailable(QuizTypeSelectActivity.this)) {
                    tvMsg.setText(R.string.unlock_no_internet);
                    btnWatchAd.setEnabled(false);
                    btnWatchAd.setAlpha(0.5f);
                } else if (AdManager.isRewardedAdReadyToShow()) {
                    tvMsg.setText(R.string.unlock_message);
                    btnWatchAd.setEnabled(true);
                    btnWatchAd.setAlpha(1.0f);
                } else {
                    if (!AdManager.isRewardedAdLoaded() && !AdManager.isRewardedAdLoading()) {
                        AdManager.loadRewardedAd(QuizTypeSelectActivity.this);
                    }
                    tvMsg.setText(R.string.unlock_wait_message);
                    btnWatchAd.setEnabled(false);
                    btnWatchAd.setAlpha(0.5f);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateStatus);

        btnWatchAd.setOnClickListener(v -> {
            if (AdManager.isRewardedAdReadyToShow()) {
                AdManager.showRewardedAd(this, rewardItem -> {
                    // This is only called if the user finishes the ad and earns the reward
                    isRewardEarned = true;
                }, () -> {
                    // This is called when the ad is closed (either reward earned or not)
                    if (isRewardEarned) {
                        runOnUiThread(() -> {
                            getSharedPreferences("QuizPrefs", MODE_PRIVATE).edit()
                                    .putBoolean("unlocked_" + type, true)
                                    .apply();
                            updateLockStates();
                            if (dialog.isShowing()) dialog.dismiss();
                            startQuiz(type, false); // Skip interstitial after reward
                        });
                    }
                });
            } else {
                Toast.makeText(this, getString(R.string.ad_not_ready), Toast.LENGTH_SHORT).show();
                AdManager.loadRewardedAd(this);
            }
        });

        dialog.setOnDismissListener(d -> handler.removeCallbacksAndMessages(null));
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void startQuiz(String type, boolean showInterstitial) {
        if (!isCardClickable || isFinishing() || isDestroyed()) return;
        
        isCardClickable = false;

        Runnable launchQuiz = () -> {
            Intent intent = new Intent(QuizTypeSelectActivity.this, QuizShowActivity.class);
            intent.putExtra(getString(R.string.quiz_type_key), type);
            startActivity(intent);
        };

        if (showInterstitial) {
            AdManager.showInterstitialAd(this, launchQuiz::run);
        } else {
            launchQuiz.run();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isCardClickable = true;
        updateLockStates();
    }
}
