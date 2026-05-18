package com.factifyft.app.activities;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;

import com.factifyft.app.R;
import com.factifyft.app.utils.AdManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class QuizShowActivity extends BaseActivity {

    private TextView questionText, scoreText, questionsTextView;
    private LinearProgressIndicator progressBar;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int countQuestions = 0;

    private String[] questions;
    private String[] options;
    private String[] correctAnswers;
    private List<Integer> questionOrder;
    private List<MaterialButton> optionButtons;
    
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isAnsweringAllowed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_show);

        // Initialize Views
        questionText = findViewById(R.id.question_text);
        scoreText = findViewById(R.id.score_text);
        questionsTextView = findViewById(R.id.QuizQuestionView);
        progressBar = findViewById(R.id.quiz_progress);

        MaterialButton option1 = findViewById(R.id.option1);
        MaterialButton option2 = findViewById(R.id.option2);
        MaterialButton option3 = findViewById(R.id.option3);
        MaterialButton option4 = findViewById(R.id.option4);
        
        optionButtons = new ArrayList<>(Arrays.asList(option1, option2, option3, option4));

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AdManager.showInterstitialAd(QuizShowActivity.this, QuizShowActivity.this::finish);
            }
        });

        AdManager.initBannerAd(findViewById(R.id.ad_container));

        // Load Quiz Data
        String quizType = getIntent().getStringExtra(getString(R.string.quiz_type_key));
        if (quizType == null) quizType = getString(R.string.default_quiz_type);
        
        loadQuizResources(quizType.trim().toLowerCase(Locale.ROOT));

        // Shuffle Question Order
        if (questions != null) {
            questionOrder = new ArrayList<>();
            for (int i = 0; i < questions.length; i++) {
                questionOrder.add(i);
            }
            Collections.shuffle(questionOrder);
            loadQuestion();
        }

        // Click Listeners
        for (MaterialButton button : optionButtons) {
            button.setOnClickListener(v -> {
                if (!isAnsweringAllowed) return;
                isAnsweringAllowed = false;
                
                for (MaterialButton b : optionButtons) {
                    b.setEnabled(false);
                }
                checkAnswer(button);
            });
        }
    }

    private void loadQuizResources(String type) {
        if (type.equalsIgnoreCase(getString(R.string.quiz_psychology))) {
            questions = getResources().getStringArray(R.array.psychologyQuestions);
            options = getResources().getStringArray(R.array.psychologyOptions);
            correctAnswers = getResources().getStringArray(R.array.psychologyAnswers);
        } else if (type.equalsIgnoreCase(getString(R.string.quiz_technology))) {
            questions = getResources().getStringArray(R.array.technologyQuestions);
            options = getResources().getStringArray(R.array.technologyOptions);
            correctAnswers = getResources().getStringArray(R.array.technologyAnswers);
        } else if (type.equalsIgnoreCase(getString(R.string.quiz_universe))) {
            questions = getResources().getStringArray(R.array.universeQuestions);
            options = getResources().getStringArray(R.array.universeOptions);
            correctAnswers = getResources().getStringArray(R.array.universeAnswers);
        } else if (type.equalsIgnoreCase(getString(R.string.quiz_science))) {
            questions = getResources().getStringArray(R.array.basic_science_questions);
            options = getResources().getStringArray(R.array.basic_science_options);
            correctAnswers = getResources().getStringArray(R.array.basic_science_answers);
        } else if (type.equalsIgnoreCase(getString(R.string.quiz_sports))) {
            questions = getResources().getStringArray(R.array.sports_basics_questions);
            options = getResources().getStringArray(R.array.sports_basics_options);
            correctAnswers = getResources().getStringArray(R.array.sports_basics_answers);
        } else if (type.equalsIgnoreCase(getString(R.string.quiz_general_knowledge))) {
            questions = getResources().getStringArray(R.array.general_knowledge_questions);
            options = getResources().getStringArray(R.array.general_knowledge_options);
            correctAnswers = getResources().getStringArray(R.array.general_knowledge_answers);
        } else {
            questions = new String[]{getString(R.string.quiz_not_found)};
            options = new String[]{getString(R.string.quiz_default_options)};
            correctAnswers = new String[]{getString(R.string.quiz_default_correct)};
        }
    }

    private void checkAnswer(MaterialButton selectedButton) {
        if (questions == null || correctAnswers == null || options == null) return;

        int actualIndex = questionOrder.get(currentQuestionIndex);
        String selected = selectedButton.getText().toString().trim();
        String correct = correctAnswers[actualIndex].trim();

        if (selected.equalsIgnoreCase(correct)) {
            selectedButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)));
            score += 10;
        } else {
            selectedButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.quiz_wrong_red)));
            for (MaterialButton button : optionButtons) {
                if (button.getText().toString().trim().equalsIgnoreCase(correct)) {
                    button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)));
                    break;
                }
            }
        }

        handler.postDelayed(() -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.length) {
                loadQuestion();
                for (MaterialButton b : optionButtons) {
                    b.setEnabled(true);
                }
                isAnsweringAllowed = true;
            } else {
                showFinalScore();
            }
        }, 600);
    }

    private void loadQuestion() {
        if (questions == null || currentQuestionIndex >= questions.length) {
            showFinalScore();
            return;
        }

        // Reset Colors
        ColorStateList defaultColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.quiz_option_default));
        for (MaterialButton b : optionButtons) {
            b.setBackgroundTintList(defaultColor);
        }

        countQuestions++;
        int actualIndex = questionOrder.get(currentQuestionIndex);
        questionText.setText(questions[actualIndex]);

        String[] optionsArray = options[actualIndex].split("\\|");
        List<String> optionsList = new ArrayList<>(Arrays.asList(optionsArray));
        Collections.shuffle(optionsList);

        for (int i = 0; i < optionButtons.size(); i++) {
            if (i < optionsList.size()) {
                optionButtons.get(i).setText(optionsList.get(i).trim());
                optionButtons.get(i).setVisibility(View.VISIBLE);
            } else {
                optionButtons.get(i).setVisibility(View.GONE);
            }
        }

        scoreText.setText(getString(R.string.score_view, this.score));
        questionsTextView.setText(getString(R.string.questions_progress, countQuestions, questions.length));

        if (progressBar != null) {
            progressBar.setMax(questions.length);
            progressBar.setProgress(countQuestions);
        }
    }

    private void showFinalScore() {
        if (isFinishing() || isDestroyed()) return;
        Dialog dialog = createQuizCompletedDialog(score);
        dialog.show();
    }

    private Dialog createQuizCompletedDialog(int score) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_quiz_complete, findViewById(android.R.id.content), false);

        TextView scoreView = dialogView.findViewById(R.id.dialogQuizScore);
        TextView returnBtn = dialogView.findViewById(R.id.returnBtn);
        MaterialButton quizRestartBtn = dialogView.findViewById(R.id.quizRestartBtn);
        ImageView img = dialogView.findViewById(R.id.quizImage);
        TextView userPerformance = dialogView.findViewById(R.id.userPerformance);

        int totalPossible = questions.length * 10;
        scoreView.setText(getString(R.string.quiz_score_format, score, totalPossible));

        if (score == totalPossible) {
            userPerformance.setText(getString(R.string.full_score));
            img.setImageResource(R.drawable.full_score_image);
        } else if (score <= (totalPossible / 2)) {
            userPerformance.setText(getString(R.string.better_luck_next_time));
            img.setImageResource(R.drawable.quiz_better_next_time);
        } else {
            userPerformance.setText(getString(R.string.keep_practicing));
            img.setImageResource(R.drawable.quiz_score_dialogue);
        }

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        dialog.getWindow().setLayout((int) (metrics.widthPixels * 0.9), (int) (metrics.heightPixels * 0.6));
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        returnBtn.setOnClickListener(v -> {
            dialog.dismiss();
            AdManager.showInterstitialAd(this, this::finish);
        });

        quizRestartBtn.setOnClickListener(v -> {
            dialog.dismiss();
            AdManager.showRewardedAd(this, reward -> {}, this::restartQuiz);
        });

        return dialog;
    }

    private void restartQuiz() {
        currentQuestionIndex = 0;
        score = 0;
        countQuestions = 0;
        isAnsweringAllowed = true;
        Collections.shuffle(questionOrder);
        
        for (MaterialButton b : optionButtons) {
            b.setEnabled(true);
            b.setVisibility(View.VISIBLE);
        }
        
        loadQuestion();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
