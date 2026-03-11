package com.factifyft.app.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.factifyft.app.R;
import com.factifyft.app.favoritePackage.FavoriteAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textEmpty;
   private ImageView noFavImage;
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
        noFavImage = view.findViewById(R.id.noFavImage);


        prefs = requireContext().getSharedPreferences(getString(R.string.prefs_facts), Context.MODE_PRIVATE);
//        prefs = requireContext().getSharedPreferences("facts_prefs", Context.MODE_PRIVATE);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FavoriteAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        loadFavorites();

        // Listen for changes in SharedPreferences
        listener = (sharedPrefs, key) -> {
            if (getString(R.string.prefs_fav_set).equals(key)) {
                loadFavorites();
            }
//            if ("fav_set".equals(key)) loadFavorites();
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);

        return view;
    }

    private void loadFavorites() {
        Set<String> favSet = new HashSet<>(prefs.getStringSet(getString(R.string.prefs_fav_set), new HashSet<>()));
//        Set<String> favSet = new HashSet<>(prefs.getStringSet("fav_set", new HashSet<>()));
        List<String> favList = new ArrayList<>(favSet);

        if (favList.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
            noFavImage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textEmpty.setVisibility(View.GONE);
            noFavImage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new FavoriteAdapter(requireContext(), favList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listener != null) {
            prefs.unregisterOnSharedPreferenceChangeListener(listener);
        }
    }
}