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
import com.example.realtimechatapplication.api.dto.RegisterRequestDto;
import com.example.realtimechatapplication.api.dto.UserProfileResponseDto;
import com.example.realtimechatapplication.models.UserModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class RegistrationFragment extends Fragment {

    private TextInputEditText usernameInput, emailInput, passwordInput;
    private MainViewModel mainViewModel;

    @Inject
    ApiService apiService;

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    public RegistrationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        usernameInput = view.findViewById(R.id.UsernameInput);
        emailInput = view.findViewById(R.id.EmailInput);
        passwordInput = view.findViewById(R.id.PasswordInput);
        MaterialButton registerButton = view.findViewById(R.id.RegisterButton);
        TextView backToLoginLink = view.findViewById(R.id.BackToLoginLink);

        registerButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Kérlek tölts ki minden mezőt!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(password)) {
                Toast.makeText(getContext(), "A jelszónak legalább 8 karakteresnek kell lennie, tartalmaznia kell kis- és nagybetűt, számot és speciális karaktert (@#$%^&+=), szóköz nélkül!", Toast.LENGTH_LONG).show();
                return;
            }

            performRegistration(username, email, password, view);
        });

        backToLoginLink.setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });
    }

    private boolean isValidPassword(String password) {
        return Pattern.compile(PASSWORD_PATTERN).matcher(password).matches();
    }

    private void performRegistration(String username, String email, String password, View view) {
        mainViewModel.setIsLoading(true);

        RegisterRequestDto registerRequest = new RegisterRequestDto(username, email, password);

        apiService.register(registerRequest).enqueue(new Callback<AuthResponseDto>() {
            @Override
            public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    RetrofitClient.setAuthToken(token);
                    
                    // Most kérjük le a profiladatokat
                    fetchUserProfile(view);
                } else {
                    mainViewModel.setIsLoading(false);
                    String serverMessage = "Regisztráció sikertelen!";
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            if (jsonObject.has("error")) serverMessage = jsonObject.getString("error");
                        }
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Error parsing error body", e);
                    }
                    Toast.makeText(getContext(), serverMessage, Toast.LENGTH_LONG).show();
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

                    Toast.makeText(getContext(), "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();

                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.loginFragment, true)
                            .build();
                    
                    Navigation.findNavController(view).navigate(R.id.chatsFragment, null, navOptions);
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponseDto> call, Throwable t) {
                mainViewModel.setIsLoading(false);
                Log.e("Registration", "Failed to fetch profile", t);
            }
        });
    }
}
