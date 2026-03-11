package com.factifyft.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class overView_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_over_view_screen);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(overView_screen.this, HomeScreen.class));
            finish();
        }, 3000);
    }
}