package com.factifyft.app.activities;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.factifyft.app.R;
import com.factifyft.app.fragments.DailyFragment;
import com.factifyft.app.fragments.FactsFragment;
import com.factifyft.app.fragments.FavoriteFragment;
import com.factifyft.app.fragments.GamesFragment;
import com.factifyft.app.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashBoardActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment currentFragment;
   private final DailyFragment dailyFragment = new DailyFragment();
    private final FactsFragment factsFragment = new FactsFragment();
    private final GamesFragment gamesFragment = new GamesFragment();
    private final FavoriteFragment favoriteFragment = new FavoriteFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment (Home)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, dailyFragment)
                .commit();
        bottomNavigationView.setSelectedItemId(R.id.bottom_daily);
        currentFragment = dailyFragment;

        // Handle bottom navigation clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;
            if (id == R.id.bottom_daily) selectedFragment = dailyFragment;
            else if (id == R.id.bottom_facts) selectedFragment = factsFragment;
            else if (id == R.id.bottom_games) selectedFragment = gamesFragment;
            else if (id == R.id.bottom_favorite) selectedFragment = favoriteFragment;
            else if (id == R.id.bottom_profile) selectedFragment = profileFragment;

            if (selectedFragment != null && selectedFragment != currentFragment) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, selectedFragment)
                        .commit();
                currentFragment = selectedFragment;
            }
            return true;
        });

        // Handle back button properly
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // If not on home, go back to home
                if (currentFragment != dailyFragment) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, dailyFragment)
                            .commit();
                    bottomNavigationView.setSelectedItemId(R.id.bottom_daily);
                    currentFragment = dailyFragment;
                } else {
                    // If already home, close app
                    finish();
                }
            }
        });
    }
}
