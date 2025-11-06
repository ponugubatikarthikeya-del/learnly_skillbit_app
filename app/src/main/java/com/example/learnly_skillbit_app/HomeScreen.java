package com.example.learnly_skillbit_app;

import android.os.Bundle;
import android.view.WindowManager;


import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.learnly_skillbit_app.fragments.DailyFragment;
import com.example.learnly_skillbit_app.fragments.FactsFragment;
import com.example.learnly_skillbit_app.fragments.FavoriteFragment;
import com.example.learnly_skillbit_app.fragments.GamesFragment;
import com.example.learnly_skillbit_app.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreen extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment currentFragment;
    DailyFragment dailyFragment = new DailyFragment();
    FactsFragment factsFragment = new FactsFragment();
    GamesFragment gamesFragment = new GamesFragment();
    FavoriteFragment favoriteFragment = new FavoriteFragment();
    ProfileFragment profileFragment = new ProfileFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_screen);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

//
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1
//            );}

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
