package com.example.realtimechatapplication.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.realtimechatapplication.MainViewModel;
import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.api.ApiService;
import com.example.realtimechatapplication.api.RetrofitClient;
import com.example.realtimechatapplication.api.dto.AuthResponseDto;
import com.example.realtimechatapplication.api.dto.LoginRequestDto;
import com.example.realtimechatapplication.api.dto.UserProfileResponseDto;
import com.example.realtimechatapplication.models.UserModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private TextInputEditText emailInput, passwordInput;
    private MainViewModel mainViewModel;

    @Inject
    ApiService apiService;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        emailInput = view.findViewById(R.id.UsernameInput);
        passwordInput = view.findViewById(R.id.PasswordInput);
        MaterialButton loginButton = view.findViewById(R.id.LoginButton);
        TextView registrationLink = view.findViewById(R.id.RegistrationLink);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Kérlek tölts ki minden mezőt!", Toast.LENGTH_SHORT).show();
                return;
            }

            performLogin(email, password, view);
        });

        registrationLink.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.registrationFragment);
        });
    }

    private void performLogin(String email, String password, View view) {
        mainViewModel.setIsLoading(true);

        LoginRequestDto loginRequest = new LoginRequestDto(email, password);
        
        apiService.login(loginRequest).enqueue(new Callback<AuthResponseDto>() {
            @Override
            public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    RetrofitClient.setAuthToken(token);
                    // Token megvan, most kérjük le a profiladatokat
                    fetchUserProfile(view);
                } else {
                    mainViewModel.setIsLoading(false);
                    Toast.makeText(getContext(), "Hibás email vagy jelszó!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                mainViewModel.setIsLoading(false);
                Toast.makeText(getContext(), "Hálózati hiba: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserProfile(View view) {
        apiService.getCurrentUser().enqueue(new Callback<UserProfileResponseDto>() {
            @Override
            public void onResponse(Call<UserProfileResponseDto> call, Response<UserProfileResponseDto> response) {
                mainViewModel.setIsLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponseDto dto = response.body();
                    UserModel user = new UserModel(dto.getId().toString(), dto.getUsername(), "");
                    mainViewModel.setCurrentUser(user);

                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.loginFragment, true)
                            .build();
                    
                    Navigation.findNavController(view).navigate(R.id.chatsFragment, null, navOptions);
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponseDto> call, Throwable t) {
                mainViewModel.setIsLoading(false);
                Log.e("Login", "Failed to fetch profile", t);
            }
        });
    }
}
