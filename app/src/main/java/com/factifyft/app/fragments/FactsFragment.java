package com.factifyft.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.factifyft.app.R;
import com.factifyft.app.showingFactsScreen.FactCategory;
import com.factifyft.app.showingFactsScreen.FactsCategoryAdapter;
import com.factifyft.app.showingFactsScreen.FactsDisplayActivity;

import java.util.ArrayList;
import java.util.List;

public class FactsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facts, container, false);

        TextView tvUsername = view.findViewById(R.id.HelloNameView);
        SharedPreferences sp = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        String username = sp.getString("username", "Buddy");

        tvUsername.setText(getString(R.string.welcome_user, username));

        RecyclerView recyclerInteresting = view.findViewById(R.id.recyclerInteresting);
        RecyclerView recyclerPsychology = view.findViewById(R.id.recyclerPsychology);
        RecyclerView recyclerUnknown = view.findViewById(R.id.recyclerUnKnown);

        //set same height and width for recycler view
        DisplayMetrics displayMetrics =new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
//        int height = displayMetrics.heightPixels;


        recyclerInteresting.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerPsychology.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerUnknown.setLayoutManager(new GridLayoutManager(requireContext(), 2));


        // --- Interesting Facts ---
        List<FactCategory> interestingList = new ArrayList<>();
        interestingList.add(new FactCategory(getString(R.string.cat_oceans), R.drawable.ocean_bg, getString(R.string.cat_interesting)));
        interestingList.add(new FactCategory(getString(R.string.cat_space), R.drawable.space_bg, getString(R.string.cat_interesting)));
        interestingList.add(new FactCategory(getString(R.string.cat_time_universe), R.drawable.time_bg,  getString(R.string.cat_unknown)));
        interestingList.add(new FactCategory(getString(R.string.cat_mind), R.drawable.mind_bg , getString(R.string.cat_interesting)));

        // --- Psychology Facts ---
        List<FactCategory> psychologyList = new ArrayList<>();
        psychologyList.add(new FactCategory(getString(R.string.cat_men), R.drawable.men_bg, getString(R.string.cat_psychology)));
        psychologyList.add(new FactCategory(getString(R.string.cat_women), R.drawable.women_bg,  getString(R.string.cat_psychology)));
        psychologyList.add(new FactCategory(getString(R.string.cat_love), R.drawable.love_bg,  getString(R.string.cat_psychology)));

        // --- Unknown Facts ---
        List<FactCategory> unknownList = new ArrayList<>();
        unknownList.add(new FactCategory(getString(R.string.cat_nature_wonders), R.drawable.nature_bg, getString(R.string.cat_unknown)));
        unknownList.add(new FactCategory(getString(R.string.cat_mysteries),R.drawable.mystery_bg, getString(R.string.cat_unknown)));
        unknownList.add(new FactCategory(getString(R.string.cat_animals), R.drawable.animals_bg, getString(R.string.cat_unknown)));


        FactsCategoryAdapter adapterInteresting = new FactsCategoryAdapter(requireContext(), interestingList, category -> {
            Intent i = new Intent(requireContext(), FactsDisplayActivity.class);
            i.putExtra(getString(R.string.cat_category), category.getName());
            startActivity(i);
        },width);

        FactsCategoryAdapter adapterPsychology = new FactsCategoryAdapter(requireContext(), psychologyList, category -> {
            Intent i = new Intent(requireContext(), FactsDisplayActivity.class);
            i.putExtra(getString(R.string.cat_category), category.getName());
            startActivity(i);
        },width);


        FactsCategoryAdapter adapterUnknown = new FactsCategoryAdapter(requireContext(), unknownList, category -> {
            Intent i = new Intent(requireContext(), FactsDisplayActivity.class);
            i.putExtra(getString(R.string.cat_category), category.getName());
            startActivity(i);
        },width);


        recyclerInteresting.setAdapter(adapterInteresting);
        recyclerPsychology.setAdapter(adapterPsychology);
        recyclerUnknown.setAdapter(adapterUnknown);

        return view;
    }
}