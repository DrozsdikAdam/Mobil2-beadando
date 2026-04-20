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
import com.example.realtimechatapplication.api.RetrofitClient;
import com.example.realtimechatapplication.api.dto.AuthResponseDto;
import com.example.realtimechatapplication.api.dto.RegisterRequestDto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationFragment extends Fragment {

    private TextInputEditText usernameInput, emailInput, passwordInput;
    private MainViewModel mainViewModel;

    // Backend regex szigorú illesztéssel
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    public RegistrationFragment() {
        // Required empty public constructor
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

        RetrofitClient.getApiService().register(registerRequest).enqueue(new Callback<AuthResponseDto>() {
            @Override
            public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                mainViewModel.setIsLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    RetrofitClient.setAuthToken(token);
                    Toast.makeText(getContext(), "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();

                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.loginFragment, true)
                            .build();
                    Navigation.findNavController(view).navigate(R.id.chatsFragment, null, navOptions);
                } else {
                    String serverMessage = "Regisztráció sikertelen!";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            Log.e("API_ERROR", "Error body: " + errorJson);
                            JSONObject jsonObject = new JSONObject(errorJson);
                            if (jsonObject.has("error")) {
                                serverMessage = jsonObject.getString("error");
                            }
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
                Log.e("API_FAILURE", t.getMessage(), t);
                Toast.makeText(getContext(), "Hálózati hiba: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
