package com.example.learnly_skillbit_app;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class quiz_Activity extends AppCompatActivity {

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
        setContentView(R.layout.activity_quiz);


        questionText = findViewById(R.id.question_text);
        scoreText = findViewById(R.id.score_text);
        questionsTextView=findViewById(R.id.QuizQuestionView);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        optionButtons = List.of(option1, option2, option3, option4);

        //declaration of all quiz questions,options,answers

        String quizType = getIntent().getStringExtra("quizType");

        if(quizType == null ) quizType="psychology";

      switch (quizType.toLowerCase()) {
          case "psychology":
          questions = getResources().getStringArray(R.array.psychologyQuestions);
          options = getResources().getStringArray(R.array.psychologyOptions) ;
          correctAnswers = getResources().getStringArray(R.array.psychologyAnswers);
          break;
          case "technology":
              questions = getResources().getStringArray(R.array.technologyQuestions);
              options = getResources().getStringArray(R.array.technologyOptions);
              correctAnswers = getResources().getStringArray(R.array.technologyAnswers);
              break;

          case "body":
              questions = getResources().getStringArray(R.array.bodyQuestions);
              options = getResources().getStringArray(R.array.bodyOptions);
              correctAnswers = getResources().getStringArray(R.array.bodyAnswers);
              break;


          case "universe":
              questions = getResources().getStringArray(R.array.universeQuestions);
              options = getResources().getStringArray(R.array.universeOptions);
              correctAnswers = getResources().getStringArray(R.array.universeAnswers);
              break;

          default:
              questions = new String[]{"No quiz found!"};
              options = new String[]{"Option1 | Option2 | Option3 | Option4"};
              correctAnswers = new String[]{"Option1"};
             break;

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
        int actualIndex=questionOrder.get(currentQuestionIndex);
        String selected=selectedButton.getText().toString().trim();
        String correct =correctAnswers[actualIndex].trim() ;

        if (selected.contains(correct)) {
            selectedButton.setBackgroundColor(Color.GREEN);
            score+=10;
        } else {
            selectedButton.setBackgroundColor(Color.RED);
            for(MaterialButton button:option) {
                if(button.getText().toString().trim().contains(correct)) {
                    button.setBackgroundColor(Color.GREEN);
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
        }, 1000);
    }


    private void  loadQuestion() {

        if (currentQuestionIndex >= questions.length) {
            showQuizCompletedDialog(score);
            return;
        }
        // Reset option colors
        option1.setBackgroundColor(Color.parseColor("#E0E0E0"));
        option2.setBackgroundColor(Color.parseColor("#E0E0E0"));
        option3.setBackgroundColor(Color.parseColor("#E0E0E0"));
        option4.setBackgroundColor(Color.parseColor("#E0E0E0"));

        //count no of questions view
        countQuestions++;

        // Load question and options
        int actualIndex=questionOrder.get(currentQuestionIndex);
        questionText.setText(questions[actualIndex]);

       String[] opts=options[actualIndex].split("\\|");
       for(int i=0;i<optionButtons.size();i++) {
           optionButtons.get(i).setText(opts[i].trim());
       }
        // Update score text
        String score = String.valueOf(this.score);
        scoreText.setText("Score: " + score);
        questionsTextView.setText("Questions " +  countQuestions + "/" + questions.length);
    }

        private void showFinalScore() {
         Dialog dialog=showQuizCompletedDialog(score);
        dialog.show();
    }
    private Dialog showQuizCompletedDialog(int score) {
     //inflate the custom view
        LayoutInflater inflater=LayoutInflater.from(this);
        View dialogView=inflater.inflate(R.layout.quiz_dialogue_designfile,null);

        // find views in the dialog
        TextView scoreView=dialogView.findViewById(R.id.dialogQuizScore);
        TextView returnBtn=dialogView.findViewById(R.id.returnBtn);
        ImageView img=dialogView.findViewById(R.id.quizImage);
        TextView userPerformance=dialogView.findViewById(R.id.userPerformance);

        //customize text dynamically
        scoreView.setText(score + " / " + questions.length*10);
        if(score<=50){
            userPerformance.setText("Better Luck Next Time!");
        }else{
            userPerformance.setText("Keep practicing!! 💪");
        }

        //image changing by score
        if(score<=50){
            img.setImageResource(R.drawable.quiz_better_next_time);
        }else{
            img.setImageResource(R.drawable.quiz_score_dialogue);

        }

        //create dialog
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false); //outside dialog cannot touchable

        //card animation and size
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
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
