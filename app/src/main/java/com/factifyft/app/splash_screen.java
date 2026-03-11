package com.factifyft.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_screen);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(splash_screen.this, overView_screen.class);
            startActivity(intent);
            finish();
        },2000);
    }
}