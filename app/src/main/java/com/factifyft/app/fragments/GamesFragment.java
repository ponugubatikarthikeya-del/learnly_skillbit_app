package com.factifyft.app.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.factifyft.app.activities.TicGameTypeSelectActivity;
import com.factifyft.app.activities.QuizTypeSelectActivity;
import com.factifyft.app.activities.snakeGameActivity;
import com.factifyft.app.R;
import com.google.android.material.card.MaterialCardView;

public class GamesFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
View view= inflater.inflate(R.layout.fragment_games, container, false);

        MaterialCardView btnQuiz= view.findViewById(R.id.QuizPlay);
        MaterialCardView btnSnake= view.findViewById(R.id.snakeGamePlay);
        MaterialCardView btnTic= view.findViewById(R.id.ticTacToyPlay);

        //going to quiz activity
        btnQuiz.setOnClickListener(view1 -> {
            Intent intent=new Intent(requireActivity(), QuizTypeSelectActivity.class);
            startActivity(intent);
        });

        //going to snake game
      btnSnake.setOnClickListener(view1 -> {
          Intent intent=new Intent(requireActivity(), snakeGameActivity.class);
          startActivity(intent);
      });

     btnTic.setOnClickListener(view1 -> {
         Intent intent=new Intent(requireActivity(), TicGameTypeSelectActivity.class);
         startActivity(intent);
     });

        return view;
    }
}