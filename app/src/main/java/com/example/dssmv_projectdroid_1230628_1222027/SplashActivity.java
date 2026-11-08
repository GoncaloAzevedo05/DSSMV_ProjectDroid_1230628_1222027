package com.example.dssmv_projectdroid_1230628_1222027;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
        boolean isDarkMode = prefsManager.loadDarkModeState();
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (prefsManager.isUserLoggedIn()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("USER_EMAIL", prefsManager.getUserEmail());
                intent.putExtra("USER_NAME", prefsManager.getUserName());
                intent.putExtra("USER_CREATED_AT", prefsManager.getUserCreatedAt());
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }, 1500);
    }
}