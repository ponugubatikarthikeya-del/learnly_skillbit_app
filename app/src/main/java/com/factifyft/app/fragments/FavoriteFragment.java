package com.factifyft.app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.factifyft.app.R;
import com.factifyft.app.adapters.FavoriteAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private View emptyState;
    private FavoriteAdapter adapter;
    private final List<String> favList = new ArrayList<>();
    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerView = view.findViewById(R.id.recyclerFavorites);
        emptyState = view.findViewById(R.id.empty_state);

        prefs = requireContext().getSharedPreferences(getString(R.string.prefs_facts), Context.MODE_PRIVATE);

        // Initialize adapter with empty list
        adapter = new FavoriteAdapter(requireContext(), favList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Define the listener
        listener = (sharedPrefs, key) -> {
            if (isAdded() && getString(R.string.prefs_fav_set).equals(key)) {
                // Use post to ensure UI updates on main thread immediately
                view.post(this::loadFavorites);
            }
        };

        return view;
    }

    private void loadFavorites() {
        if (!isAdded()) return;

        // CRITICAL FIX: Always create a NEW HashSet.
        // SharedPreferences won't detect changes if you modify the original set.
        Set<String> favSet = prefs.getStringSet(getString(R.string.prefs_fav_set), null);

        favList.clear();
        if (favSet != null && !favSet.isEmpty()) {
            favList.addAll(favSet);
            recyclerView.setVisibility(View.VISIBLE);
            if (emptyState != null) emptyState.setVisibility(View.GONE);
        } else {
            // Show empty state image
            recyclerView.setVisibility(View.GONE);
            if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register listener
        prefs.registerOnSharedPreferenceChangeListener(listener);
        // Always force a fresh load when coming back
        loadFavorites();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister to prevent memory leaks
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
