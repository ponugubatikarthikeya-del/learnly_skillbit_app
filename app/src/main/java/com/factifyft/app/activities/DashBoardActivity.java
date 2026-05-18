package com.factifyft.app.activities;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.factifyft.app.R;
import com.factifyft.app.fragments.DailyFragment;
import com.factifyft.app.fragments.FactsFragment;
import com.factifyft.app.fragments.FavoriteFragment;
import com.factifyft.app.fragments.GamesFragment;
import com.factifyft.app.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashBoardActivity extends BaseActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Handle initial fragment loading
        if (savedInstanceState == null) {
            loadFragment(new DailyFragment(), "DAILY");
            bottomNavigationView.setSelectedItemId(R.id.bottom_daily);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;
            String tag = "";

            if (id == R.id.bottom_daily) {
                selectedFragment = new DailyFragment();
                tag = "DAILY";
            } else if (id == R.id.bottom_facts) {
                selectedFragment = new FactsFragment();
                tag = "FACTS";
            } else if (id == R.id.bottom_games) {
                selectedFragment = new GamesFragment();
                tag = "GAMES";
            } else if (id == R.id.bottom_favorite) {
                selectedFragment = new FavoriteFragment();
                tag = "FAVORITE";
            } else if (id == R.id.bottom_profile) {
                selectedFragment = new ProfileFragment();
                tag = "PROFILE";
            }

            if (selectedFragment != null) {
                // Check if we are already showing this fragment type to avoid redundant reloads
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.container);
                if (current != null && current.getClass().equals(selectedFragment.getClass())) {
                    return true;
                }
                loadFragment(selectedFragment, tag);
            }
            return true;
        });

        // Handle back button: If not on Daily screen, go to Daily. If on Daily, exit.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.container);
                if (!(current instanceof DailyFragment)) {
                    loadFragment(new DailyFragment(), "DAILY");
                    bottomNavigationView.setSelectedItemId(R.id.bottom_daily);
                } else {
                    finish();
                }
            }
        });
    }

    private void loadFragment(@NonNull Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.container, fragment, tag)
                .commit();
    }
}
