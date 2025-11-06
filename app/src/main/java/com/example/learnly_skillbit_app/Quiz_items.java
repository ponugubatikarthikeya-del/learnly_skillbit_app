package com.example.learnly_skillbit_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class Quiz_items extends AppCompatActivity {

    MaterialCardView quizPsycology,quizTechnology,quizBody,quizUniverse;
    private boolean isCardClickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.quiz_items);

        TextView psycoogyText= findViewById(R.id.psychologyText);
        TextView technologyText= findViewById(R.id.technologyText);
        TextView bodyText= findViewById(R.id.bodyText);
        TextView UniverseText= findViewById(R.id.UniverseText);
        ImageButton QuizButtonBack=findViewById(R.id.quizBackButton);
       quizPsycology=findViewById(R.id.quizPsychology);
       quizTechnology=findViewById(R.id.quizTechnology);
       quizBody=findViewById(R.id.quizBody);
       quizUniverse=findViewById(R.id.quizUniverse);


        quizPsycology.setOnClickListener(view -> {
            if(!isCardClickable) return;

            isCardClickable = false;
            Intent intent = new Intent(Quiz_items.this, quiz_Activity.class);
            intent.putExtra("quizType", psycoogyText.getText().toString());
            startActivity(intent);
        });

        quizTechnology.setOnClickListener(view -> {
            if(!isCardClickable) return;

            isCardClickable = false;
            Intent intent = new Intent(Quiz_items.this, quiz_Activity.class);
            intent.putExtra("quizType", technologyText.getText().toString());
            startActivity(intent);
        });

        quizBody.setOnClickListener(view -> {
            if(!isCardClickable) return;

            isCardClickable = false;
            Intent intent = new Intent(Quiz_items.this, quiz_Activity.class);
            intent.putExtra("quizType", bodyText.getText().toString());
            startActivity(intent);
        });

        quizUniverse.setOnClickListener(view -> {
            if(!isCardClickable) return;

            isCardClickable = false; //block further cards
            Intent intent = new Intent(Quiz_items.this, quiz_Activity.class);
            intent.putExtra("quizType", UniverseText.getText().toString());
            startActivity(intent);
        });
        QuizButtonBack.setOnClickListener(view -> {
            finish();
        });


    }

    protected void onResume(){
        super.onResume();
        isCardClickable = true; //allow clicks again
    }
}