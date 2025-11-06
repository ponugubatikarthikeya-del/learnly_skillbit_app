package com.example.learnly_skillbit_app.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learnly_skillbit_app.Quiz_items;
import com.example.learnly_skillbit_app.snakeGameActivity;
import com.example.learnly_skillbit_app.R;
import com.example.learnly_skillbit_app.quiz_Activity;
import com.google.android.material.card.MaterialCardView;

public class GamesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
View view= inflater.inflate(R.layout.fragment_games, container, false);

        MaterialCardView btnQuiz= view.findViewById(R.id.QuizPlay);
        MaterialCardView btnSnake= view.findViewById(R.id.snakeGamePlay);


        //going to quiz activity
        btnQuiz.setOnClickListener(view1 -> {
            Intent intent=new Intent(getActivity(), Quiz_items.class);
            startActivity(intent);
        });

        //going to snake game
      btnSnake.setOnClickListener(view1 -> {
          Intent intent=new Intent(getActivity(), snakeGameActivity.class);
          startActivity(intent);
      });


        return view;
    }
}