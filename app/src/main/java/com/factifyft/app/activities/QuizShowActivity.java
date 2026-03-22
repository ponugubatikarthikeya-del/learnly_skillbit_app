package com.factifyft.app.activities;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.factifyft.app.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class QuizShowActivity extends AppCompatActivity {

    private TextView questionText, scoreText, questionsTextView;
    private MaterialButton option1, option2, option3, option4;
    private int currentQuestionIndex = 0;
    private int score = 0;

    private int countQuestions=0;

    private String[] questions;
    private String[] options;
    private String[] correctAnswers;
    private List<Integer> questionOrder;
    private List<MaterialButton> optionButtons;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_show);


        questionText = findViewById(R.id.question_text);
        scoreText = findViewById(R.id.score_text);
        questionsTextView=findViewById(R.id.QuizQuestionView);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        optionButtons = new ArrayList<>(Arrays.asList(option1, option2, option3, option4));

        //declaration of all quiz questions,options,answers
        String quizType = getIntent().getStringExtra(getString(R.string.quiz_type_key));

        if (quizType == null) quizType = getString(R.string.default_quiz_type);
        quizType = quizType.trim().toLowerCase(Locale.ROOT);

        String qt = quizType.toLowerCase(Locale.ROOT);

        if (qt.equalsIgnoreCase(getString(R.string.quiz_psychology))) {
            questions = getResources().getStringArray(R.array.psychologyQuestions);
            options = getResources().getStringArray(R.array.psychologyOptions);
            correctAnswers = getResources().getStringArray(R.array.psychologyAnswers);

        } else if (qt.equalsIgnoreCase(getString(R.string.quiz_technology))) {
            questions = getResources().getStringArray(R.array.technologyQuestions);
            options = getResources().getStringArray(R.array.technologyOptions);
            correctAnswers = getResources().getStringArray(R.array.technologyAnswers);

        } else if (qt.equalsIgnoreCase(getString(R.string.quiz_universe))) {
            questions = getResources().getStringArray(R.array.universeQuestions);
            options = getResources().getStringArray(R.array.universeOptions);
            correctAnswers = getResources().getStringArray(R.array.universeAnswers);

        } else {
            questions = new String[]{ getString(R.string.quiz_not_found) };
            options = new String[]{ getString(R.string.quiz_default_options) };
            correctAnswers = new String[]{ getString(R.string.quiz_default_correct) };
        }

        questionOrder = new ArrayList<>();
        for(int i=0;i<questions.length;i++) {
            questionOrder.add(i);
        }
        Collections.shuffle(questionOrder);

        loadQuestion();
        //load first question
        for (MaterialButton button : optionButtons) {
            button.setOnClickListener(v -> {
                for (MaterialButton button1 : optionButtons) {
                    button1.setEnabled(false);
                }
                checkAnswer(button, optionButtons);
            });

        }
    }

    private void checkAnswer(MaterialButton selectedButton, List<MaterialButton> option) {
        if (questions == null || correctAnswers == null || options == null) {
            return;
        }
        int actualIndex=questionOrder.get(currentQuestionIndex);
        String selected=selectedButton.getText().toString().trim().toLowerCase();
        String correct = correctAnswers[actualIndex].trim();
        if (selected.equalsIgnoreCase(correct)) {
            selectedButton.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
            );
            score+=10;
        } else {

            selectedButton.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.quiz_wrong_red))
            );
            for(MaterialButton button:option) {
                if(button.getText().toString().trim().contains(correct)) {
                   button.setBackgroundTintList(
                           ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)));
//                    button.setBackgroundColor(Color.GREEN);
                    break;
                }
            }
        }


        // Move to next question after 1 second
        selectedButton.postDelayed(() -> {
            currentQuestionIndex++;

            if (questions != null && currentQuestionIndex < questions.length) {
                loadQuestion();
                for (MaterialButton button1 : option) {
                    button1.setEnabled(true);
                }
            } else {
                // Quiz finished
                showFinalScore();
            }
        }, 500);
    }


    private void  loadQuestion() {
        if (questions == null || questions.length == 0) {
            showQuizCompletedDialog(score);
            return;
        }
        if (currentQuestionIndex >= questions.length) {
            showQuizCompletedDialog(score);
            return;
        }
        // Reset option colors
        ColorStateList grey = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.quiz_option_default)
        );
//        ColorStateList grey = ColorStateList.valueOf(Color.parseColor("#E0E0E0"));
        option1.setBackgroundTintList(grey);
        option2.setBackgroundTintList(grey);
        option3.setBackgroundTintList(grey);
        option4.setBackgroundTintList(grey);

        if (currentQuestionIndex >= questions.length) {
            showQuizCompletedDialog(score);
            return;
        }
        //count no of questions view
        countQuestions++;

        // Load question and options
        int actualIndex=questionOrder.get(currentQuestionIndex);
        questionText.setText(questions[actualIndex]);

        List<String> optionsList = new ArrayList<>(Arrays.asList(options[actualIndex].split("\\|")));
//        shuffle the answers
        Collections.shuffle(optionsList);
        //set shuffled option text on buttons
        for(int i=0;i<optionButtons.size();i++){
            optionButtons.get(i).setText(optionsList.get(i).trim());
        }

        // Update score text
        scoreText.setText(getString(R.string.score_view, this.score));
        questionsTextView.setText(
                getString(R.string.questions_progress, countQuestions, questions.length)
        );

    }
        private void showFinalScore() {
         Dialog dialog=showQuizCompletedDialog(score);
        dialog.show();
    }
    private Dialog showQuizCompletedDialog(int score) {
     //inflate the custom view
//        LayoutInflater inflater=LayoutInflater.from(this);
        View dialogView = getLayoutInflater().inflate(
                R.layout.dialog_quiz_complete,
                findViewById(android.R.id.content),
                false
        );


        // find views in the dialog
        TextView scoreView=dialogView.findViewById(R.id.dialogQuizScore);
        TextView returnBtn=dialogView.findViewById(R.id.returnBtn);
        ImageView img=dialogView.findViewById(R.id.quizImage);
        TextView userPerformance=dialogView.findViewById(R.id.userPerformance);

        //customize text dynamically
        scoreView.setText(
                getString(R.string.quiz_score_format, score, questions.length * 10)
        );
//        scoreView.setText(score + " / " + questions.length*10);
        if(score==questions.length*10){
            userPerformance.setText(getString(R.string.full_score));
        }
        else if(score<=50){
            userPerformance.setText(getString(R.string.better_luck_next_time));
        }else{
            userPerformance.setText(getString(R.string.keep_practicing));
        }

        //image changing by score
        if(score==questions.length*10){
            img.setImageResource(R.drawable.full_score_image);
        }
        if(score<=60){
            img.setImageResource(R.drawable.quiz_better_next_time);
        }else{
            img.setImageResource(R.drawable.quiz_score_dialogue);
        }

        //create dialog
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false); //outside dialog cannot touchable

        //card animation and size
        DisplayMetrics metrics=getResources().getDisplayMetrics();
        int width=(int) (metrics.widthPixels*0.925);
        int height=(int) (metrics.heightPixels*0.65);
        Objects.requireNonNull(dialog.getWindow()).setLayout(width,height);
        dialog.getWindow().getAttributes().windowAnimations=R.style.animation;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        //button listeners

        returnBtn.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        return dialog;
    }

}
