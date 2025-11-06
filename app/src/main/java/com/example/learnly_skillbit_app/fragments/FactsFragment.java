package com.example.learnly_skillbit_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnly_skillbit_app.R;
import com.example.learnly_skillbit_app.showingFactsScreen.FactCategory;
import com.example.learnly_skillbit_app.showingFactsScreen.FactsCategoryAdapter;
import com.example.learnly_skillbit_app.showingFactsScreen.FactsDisplayActivity;

import java.util.ArrayList;
import java.util.List;

public class FactsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_facts, container, false);

        RecyclerView recyclerInteresting = view.findViewById(R.id.recyclerInteresting);
        RecyclerView recyclerPsychology = view.findViewById(R.id.recyclerPsychology);
        RecyclerView recyclerUnknown = view.findViewById(R.id.recyclerUnKnown);

        //set same height and width for recycler view
        DisplayMetrics displayMetrics =new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;


        recyclerInteresting.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerPsychology.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerUnknown.setLayoutManager(new GridLayoutManager(requireContext(), 2));


        // --- Interesting Facts ---
        List<FactCategory> interestingList = new ArrayList<>();
        interestingList.add(new FactCategory("Oceans", R.drawable.ocean_bg, "Interesting"));
        interestingList.add(new FactCategory("Space", R.drawable.space_bg, "Interesting"));
        interestingList.add(new FactCategory("Fitness", R.drawable.fitness_bg, "Interesting"));
        interestingList.add(new FactCategory("Mind", R.drawable.mind_bg , "Interesting"));

        // --- Psychology Facts ---
        List<FactCategory> psychologyList = new ArrayList<>();
        psychologyList.add(new FactCategory("Men", R.drawable.men_bg, "Psychology"));
        psychologyList.add(new FactCategory("Women", R.drawable.women_bg, "Psychology"));
        psychologyList.add(new FactCategory("Love", R.drawable.love_bg, "Interesting"));

        // --- Unknown Facts ---
        List<FactCategory> unknownList = new ArrayList<>();
        unknownList.add(new FactCategory("Time & Universe", R.drawable.time_bg, "Unknown"));
        unknownList.add(new FactCategory("Nature Wonders", R.drawable.nature_bg, "Unknown"));
        unknownList.add(new FactCategory("Mysteries", R.drawable.mystery_bg, "Unknown"));
        unknownList.add(new FactCategory("Animals", R.drawable.animals_bg, "Unknown"));


        FactsCategoryAdapter adapterInteresting = new FactsCategoryAdapter(requireContext(), interestingList, category -> {
            Intent i = new Intent(requireContext(), FactsDisplayActivity.class);
            i.putExtra("category", category.getName());
            startActivity(i);
        },width,height);

        FactsCategoryAdapter adapterPsychology = new FactsCategoryAdapter(requireContext(), psychologyList, category -> {
            Intent i = new Intent(requireContext(), FactsDisplayActivity.class);
            i.putExtra("category", category.getName());
            startActivity(i);
        },width,height);


        FactsCategoryAdapter adapterUnknown = new FactsCategoryAdapter(requireContext(), unknownList, category -> {
            Intent i = new Intent(requireContext(), FactsDisplayActivity.class);
            i.putExtra("category", category.getName());
            startActivity(i);
        },width,height);


        recyclerInteresting.setAdapter(adapterInteresting);
        recyclerPsychology.setAdapter(adapterPsychology);
        recyclerUnknown.setAdapter(adapterUnknown);

        return view;
    }
}