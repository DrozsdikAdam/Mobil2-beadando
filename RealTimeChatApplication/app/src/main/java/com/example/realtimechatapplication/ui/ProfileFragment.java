package com.example.realtimechatapplication.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.realtimechatapplication.MainViewModel;
import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.models.UserModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ProfileFragment extends Fragment {

    private ImageButton backButton;
    private LinearLayout profileDetailsHeader;
    private LinearLayout profileDetailsContent;
    private ImageView dropdownArrow;
    private TextView profileName;
    private EditText editUsername, editEmail;
    private MainViewModel mainViewModel;
    private SwitchMaterial themeSwitch;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel inicializálása
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Nézetek keresése
        backButton = view.findViewById(R.id.backButton);
        profileDetailsHeader = view.findViewById(R.id.profileDetailsHeader);
        profileDetailsContent = view.findViewById(R.id.profileDetailsContent);
        dropdownArrow = view.findViewById(R.id.dropdownArrow);
        profileName = view.findViewById(R.id.profileName);
        editUsername = view.findViewById(R.id.editUsername);
        editEmail = view.findViewById(R.id.editEmail);
        themeSwitch = view.findViewById(R.id.themeSwitch);

        // Téma választó beállítása
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);
        themeSwitch.setChecked(isDarkMode);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("DarkMode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Adatok megfigyelése a ViewModel-ből
        mainViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                profileName.setText(user.getUserName());
                if (editUsername != null) editUsername.setText(user.getUserName());
            }
        });

        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                Navigation.findNavController(view).popBackStack();
            });
        }

        if (profileDetailsHeader != null) {
            profileDetailsHeader.setOnClickListener(v -> {
                if (profileDetailsContent.getVisibility() == View.GONE) {
                    profileDetailsContent.setVisibility(View.VISIBLE);
                    dropdownArrow.setImageResource(android.R.drawable.arrow_up_float);
                } else {
                    profileDetailsContent.setVisibility(View.GONE);
                    dropdownArrow.setImageResource(android.R.drawable.arrow_down_float);
                }
            });
        }
        
        // Kijelentkezés gomb kezelése (tesztnek)
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            mainViewModel.setCurrentUser(null);
            Navigation.findNavController(view).navigate(R.id.loginFragment);
        });
    }
}
