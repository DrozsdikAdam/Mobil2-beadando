package com.example.realtimechatapplication.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.realtimechatapplication.MainViewModel;
import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.api.RetrofitClient;
import com.example.realtimechatapplication.api.dto.AuthResponseDto;
import com.example.realtimechatapplication.api.dto.UpdateProfileRequestDto;
import com.example.realtimechatapplication.api.dto.UserProfileResponseDto;
import com.example.realtimechatapplication.models.UserModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ImageButton backButton;
    private LinearLayout profileDetailsHeader, profileDetailsContent;
    private LinearLayout languageHeader, languageContent;
    private ImageView dropdownArrow, languageDropdownArrow;
    private TextView profileName, currentLanguageText;
    private TextView langOptionEn, langOptionHu;
    private EditText editUsername, editEmail, editPassword;
    private MainViewModel mainViewModel;
    private SwitchMaterial themeSwitch;
    private View colorPickerView;

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
        setupColorPicker();
        
        // Először töltsük be az adatokat a szerverről, hogy biztosan frissek legyenek
        fetchFullProfile();

        mainViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                profileName.setText(user.getUserName());
                if (editUsername != null) editUsername.setText(user.getUserName());
                // Mivel a UserModel-ben nincs email, a ViewModel-ben sem tároltuk eddig külön,
                // de a fetchFullProfile() után itt is beállíthatnánk, ha bővítjük a modellt.
            }
        });

        if (backButton != null) {
            backButton.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
        }
        
        view.findViewById(R.id.btnModify).setOnClickListener(v -> updateProfile());

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            RetrofitClient.setAuthToken(null);
            mainViewModel.setCurrentUser(null);
            Navigation.findNavController(view).navigate(R.id.loginFragment);
        });
    }

    private void fetchFullProfile() {
        mainViewModel.setIsLoading(true);
        RetrofitClient.getApiService().getCurrentUser().enqueue(new Callback<UserProfileResponseDto>() {
            @Override
            public void onResponse(Call<UserProfileResponseDto> call, Response<UserProfileResponseDto> response) {
                mainViewModel.setIsLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponseDto dto = response.body();
                    // Frissítjük a ViewModel-t és a nézeteket
                    UserModel user = new UserModel(dto.getId().toString(), dto.getUsername(), "");
                    mainViewModel.setCurrentUser(user);
                    
                    // Email betöltése a szerkesztő mezőbe
                    if (editEmail != null && dto.getEmail() != null) {
                        editEmail.setText(dto.getEmail());
                    }
                    // Jelszó mező ürítése (ne mutasson placeholder-t)
                    if (editPassword != null) {
                        editPassword.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponseDto> call, Throwable t) {
                mainViewModel.setIsLoading(false);
                Log.e("Profile", "Failed to fetch profile", t);
            }
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
        editPassword = view.findViewById(R.id.editPassword);
        themeSwitch = view.findViewById(R.id.themeSwitch);
        
        languageHeader = view.findViewById(R.id.languageHeader);
        languageContent = view.findViewById(R.id.languageContent);
        languageDropdownArrow = view.findViewById(R.id.languageDropdownArrow);
        currentLanguageText = view.findViewById(R.id.currentLanguageText);
        langOptionEn = view.findViewById(R.id.langOptionEn);
        langOptionHu = view.findViewById(R.id.langOptionHu);
        colorPickerView = view.findViewById(R.id.colorPickerView);
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
        if (currentLang.isEmpty()) currentLang = "hu";
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

    private void setupColorPicker() {
        if (colorPickerView == null) return;
        
        SharedPreferences prefs = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        int savedColor = prefs.getInt("UIColor", Color.parseColor("#007ACC"));
        colorPickerView.setBackgroundColor(savedColor);

        colorPickerView.setOnClickListener(v -> {
            String[] colorNames = {"Blue", "Red", "Green", "Orange", "Purple", "Teal", "Pink", "Indigo"};
            int[] colors = {
                Color.parseColor("#007ACC"), Color.parseColor("#E53935"),
                Color.parseColor("#43A047"), Color.parseColor("#FB8C00"),
                Color.parseColor("#8E24AA"), Color.parseColor("#00897B"),
                Color.parseColor("#D81B60"), Color.parseColor("#3949AB")
            };

            ColorAdapter adapter = new ColorAdapter(requireContext(), colorNames, colors);

            new AlertDialog.Builder(requireContext())
                .setTitle(R.string.choose_color)
                .setAdapter(adapter, (dialog, which) -> {
                    int selectedColor = colors[which];
                    prefs.edit().putInt("UIColor", selectedColor).apply();
                    colorPickerView.setBackgroundColor(selectedColor);
                    requireActivity().recreate();
                })
                .show();
        });
    }

    private void updateProfile() {
        String newName = editUsername.getText().toString().trim();
        String newEmail = editEmail != null ? editEmail.getText().toString().trim() : "";
        String newPass = editPassword != null ? editPassword.getText().toString().trim() : "";
        
        if (newName.isEmpty()) {
            Toast.makeText(getContext(), "A név nem lehet üres!", Toast.LENGTH_SHORT).show();
            return;
        }

        mainViewModel.setIsLoading(true);
        UpdateProfileRequestDto request = new UpdateProfileRequestDto();
        request.setNewUsername(newName);
        if (!newEmail.isEmpty()) request.setNewEmail(newEmail);
        if (!newPass.isEmpty()) request.setNewPassword(newPass);

        RetrofitClient.getApiService().updateProfile(request).enqueue(new Callback<AuthResponseDto>() {
            @Override
            public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                mainViewModel.setIsLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Profil frissítve!", Toast.LENGTH_SHORT).show();
                    if (response.body() != null && response.body().getToken() != null && !response.body().getToken().isEmpty()) {
                        RetrofitClient.setAuthToken(response.body().getToken());
                    }
                    UserModel user = mainViewModel.getCurrentUser().getValue();
                    if (user != null) {
                        user.setUserName(newName);
                        mainViewModel.setCurrentUser(user);
                    }
                } else {
                    Toast.makeText(getContext(), "Nem sikerült a mentés!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                mainViewModel.setIsLoading(false);
                Log.e("Profile", "Update failed", t);
                Toast.makeText(getContext(), "Hálózati hiba", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class ColorAdapter extends BaseAdapter {
        private final Context context;
        private final String[] names;
        private final int[] colors;

        public ColorAdapter(Context context, String[] names, int[] colors) {
            this.context = context;
            this.names = names;
            this.colors = colors;
        }

        @Override public int getCount() { return names.length; }
        @Override public Object getItem(int position) { return names[position]; }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.color_item_layout, parent, false);
            }
            View preview = convertView.findViewById(R.id.colorPreview);
            TextView name = convertView.findViewById(R.id.colorNameText);
            
            preview.setBackgroundColor(colors[position]);
            name.setText(names[position]);
            
            return convertView;
        }
    }
}
