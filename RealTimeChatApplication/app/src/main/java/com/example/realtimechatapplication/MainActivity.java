package com.example.realtimechatapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        int selectedColor = sharedPreferences.getInt("UIColor", Color.parseColor("#007ACC"));
        applyColorTheme(selectedColor);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
    }

    private void applyColorTheme(int color) {
        switch (color) {
            case 0xFF007ACC: setTheme(R.style.Theme_RealTimeChatApplication_Blue); break;
            case 0xFFE53935: setTheme(R.style.Theme_RealTimeChatApplication_Red); break;
            case 0xFF43A047: setTheme(R.style.Theme_RealTimeChatApplication_Green); break;
            case 0xFFFB8C00: setTheme(R.style.Theme_RealTimeChatApplication_Orange); break;
            case 0xFF8E24AA: setTheme(R.style.Theme_RealTimeChatApplication_Purple); break;
            case 0xFF00897B: setTheme(R.style.Theme_RealTimeChatApplication_Teal); break;
            case 0xFFD81B60: setTheme(R.style.Theme_RealTimeChatApplication_Pink); break;
            case 0xFF3949AB: setTheme(R.style.Theme_RealTimeChatApplication_Indigo); break;
            default: setTheme(R.style.Theme_RealTimeChatApplication_Blue); break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
