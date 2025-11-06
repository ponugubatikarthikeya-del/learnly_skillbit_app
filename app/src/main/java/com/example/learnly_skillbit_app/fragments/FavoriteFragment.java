package com.example.learnly_skillbit_app.fragments;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnly_skillbit_app.R;
import com.example.learnly_skillbit_app.favoritePackage.FavoriteAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textEmpty;
    private FavoriteAdapter adapter;
    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = view.findViewById(R.id.recyclerFavorites);
        textEmpty = view.findViewById(R.id.textEmpty);

        prefs = requireContext().getSharedPreferences("facts_prefs", requireContext().MODE_PRIVATE);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FavoriteAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        loadFavorites();

        // Listen for changes in SharedPreferences
        listener = (sharedPrefs, key) -> {
            if ("fav_set".equals(key)) loadFavorites();
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);

        return view;
    }

    private void loadFavorites() {
        Set<String> favSet = prefs.getStringSet("fav_set", new HashSet<>());
        List<String> favList = new ArrayList<>(favSet);

        if (favList.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new FavoriteAdapter(requireContext(), favList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}