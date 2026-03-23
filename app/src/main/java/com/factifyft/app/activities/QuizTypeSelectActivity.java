package com.factifyft.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.factifyft.app.R;
import com.google.android.material.card.MaterialCardView;

public class QuizTypeSelectActivity extends AppCompatActivity {

    private boolean isCardClickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_type_select);

        TextView psychologyText= findViewById(R.id.psychologyText);
        TextView technologyText= findViewById(R.id.technologyText);
        TextView UniverseText= findViewById(R.id.UniverseText);
        TextView scienceText= findViewById(R.id.ScienceText);
        TextView sportsText= findViewById(R.id.sportText);
        TextView knowledgeText= findViewById(R.id.knowledgeText);


        ImageButton QuizButtonBack=findViewById(R.id.quizBackButton);
        MaterialCardView quizPsychology = findViewById(R.id.quizPsychology);
        MaterialCardView quizTechnology = findViewById(R.id.quizTechnology);
        MaterialCardView quizUniverse = findViewById(R.id.quizUniverse);
        MaterialCardView quizScience = findViewById(R.id.quizScience);
        MaterialCardView quizSports = findViewById(R.id.quizSports);
        MaterialCardView quizKnowledge = findViewById(R.id.quizKnowledge);


        quizPsychology.setOnClickListener(view -> {
            if(!isCardClickable) return;
            isCardClickable = false;
            view.postDelayed(()-> isCardClickable = true, 300);
            Intent intent = new Intent(QuizTypeSelectActivity.this, QuizShowActivity.class);
            intent.putExtra(getString(R.string.quiz_type_key), psychologyText.getText().toString());
            startActivity(intent);
        });

        quizTechnology.setOnClickListener(view -> {
            if(!isCardClickable) return;
            isCardClickable = false;
            view.postDelayed(()-> isCardClickable = true, 300);
            Intent intent = new Intent(QuizTypeSelectActivity.this, QuizShowActivity.class);
            intent.putExtra(getString(R.string.quiz_type_key), technologyText.getText().toString());
            startActivity(intent);
        });

        quizUniverse.setOnClickListener(view -> {
            if(!isCardClickable) return;
            isCardClickable = false; //block further cards
            view.postDelayed(()-> isCardClickable = true, 300);
            Intent intent = new Intent(QuizTypeSelectActivity.this, QuizShowActivity.class);
            intent.putExtra(getString(R.string.quiz_type_key), UniverseText.getText().toString());
            startActivity(intent);
        });

        quizScience.setOnClickListener(view -> {
            if(!isCardClickable) return;
            isCardClickable = false; //block further cards
            view.postDelayed(()-> isCardClickable = true, 300);
            Intent intent = new Intent(QuizTypeSelectActivity.this, QuizShowActivity.class);
            intent.putExtra(getString(R.string.quiz_type_key), scienceText.getText().toString());
            startActivity(intent);
        });

        quizSports.setOnClickListener(view -> {
            if(!isCardClickable) return;
            isCardClickable = false; //block further cards
            view.postDelayed(()-> isCardClickable = true, 300);
            Intent intent = new Intent(QuizTypeSelectActivity.this, QuizShowActivity.class);
            intent.putExtra(getString(R.string.quiz_type_key), sportsText.getText().toString());
            startActivity(intent);
        });

         quizKnowledge.setOnClickListener(view -> {
            if(!isCardClickable) return;
            isCardClickable = false; //block further cards
            view.postDelayed(()-> isCardClickable = true, 300);
            Intent intent = new Intent(QuizTypeSelectActivity.this, QuizShowActivity.class);
            intent.putExtra(getString(R.string.quiz_type_key), knowledgeText.getText().toString());
            startActivity(intent);
        });

        QuizButtonBack.setOnClickListener(view -> finish());
    }
    @Override
    protected void onResume(){
        super.onResume();
        isCardClickable = true; //allow clicks again
    }
}