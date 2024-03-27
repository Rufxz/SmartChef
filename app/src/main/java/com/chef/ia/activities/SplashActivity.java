package com.chef.ia.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;

import com.chef.ia.utils.Constants;
import com.chef.ia.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new android.os.Handler(Looper.getMainLooper()).postDelayed(
                () -> {
                    if (getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE).getBoolean(Constants.IS_FIRST_RUN, true)) {
                        startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                    finish();
                },
                1000);
    }
}