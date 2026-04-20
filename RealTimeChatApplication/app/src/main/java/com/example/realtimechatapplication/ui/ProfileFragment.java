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
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.realtimechatapplication.MainViewModel;
import com.example.realtimechatapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ProfileFragment extends Fragment {

    private ImageButton backButton;
    private LinearLayout profileDetailsHeader, profileDetailsContent;
    private LinearLayout languageHeader, languageContent;
    private ImageView dropdownArrow, languageDropdownArrow;
    private TextView profileName, currentLanguageText;
    private TextView langOptionEn, langOptionHu;
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

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        initViews(view);

        setupThemeToggle();
        setupLanguageMenu();
        setupDetailsToggle();

        mainViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                profileName.setText(user.getUserName());
                if (editUsername != null) editUsername.setText(user.getUserName());
            }
        });

        if (backButton != null) {
            backButton.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
        }
        
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            mainViewModel.setCurrentUser(null);
            Navigation.findNavController(view).navigate(R.id.loginFragment);
        });
    }

    private void initViews(View view) {
        backButton = view.findViewById(R.id.backButton);
        profileDetailsHeader = view.findViewById(R.id.profileDetailsHeader);
        profileDetailsContent = view.findViewById(R.id.profileDetailsContent);
        dropdownArrow = view.findViewById(R.id.dropdownArrow);
        profileName = view.findViewById(R.id.profileName);
        editUsername = view.findViewById(R.id.editUsername);
        editEmail = view.findViewById(R.id.editEmail);
        themeSwitch = view.findViewById(R.id.themeSwitch);
        
        languageHeader = view.findViewById(R.id.languageHeader);
        languageContent = view.findViewById(R.id.languageContent);
        languageDropdownArrow = view.findViewById(R.id.languageDropdownArrow);
        currentLanguageText = view.findViewById(R.id.currentLanguageText);
        langOptionEn = view.findViewById(R.id.langOptionEn);
        langOptionHu = view.findViewById(R.id.langOptionHu);
    }

    private void setupThemeToggle() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);
        themeSwitch.setChecked(isDarkMode);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("DarkMode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? 
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
    }

    private void setupLanguageMenu() {
        languageHeader.setOnClickListener(v -> {
            if (languageContent.getVisibility() == View.GONE) {
                languageContent.setVisibility(View.VISIBLE);
                languageDropdownArrow.setImageResource(android.R.drawable.arrow_up_float);
            } else {
                languageContent.setVisibility(View.GONE);
                languageDropdownArrow.setImageResource(android.R.drawable.arrow_down_float);
            }
        });

        langOptionEn.setOnClickListener(v -> changeLanguage("en"));
        langOptionHu.setOnClickListener(v -> changeLanguage("hu"));

        String currentLang = AppCompatDelegate.getApplicationLocales().toLanguageTags();
        if (currentLang.isEmpty()) currentLang = "hu"; // alapértelmezett
        currentLanguageText.setText(currentLang.contains("en") ? getString(R.string.lang_en) : getString(R.string.lang_hu));
    }

    private void changeLanguage(String langCode) {
        LocaleListCompat appLocales = LocaleListCompat.forLanguageTags(langCode);
        AppCompatDelegate.setApplicationLocales(appLocales);
    }

    private void setupDetailsToggle() {
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
}
