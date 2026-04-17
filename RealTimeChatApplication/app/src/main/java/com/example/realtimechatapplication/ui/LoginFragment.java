package com.example.realtimechatapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.realtimechatapplication.R;
import com.google.android.material.button.MaterialButton;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton loginButton = view.findViewById(R.id.LoginButton);
        TextView registrationLink = view.findViewById(R.id.RegistrationLink);

        loginButton.setOnClickListener(v -> {
            // Bejelentkezéskor töröljük a LoginFragment-et a visszalépési listából
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build();
            
            Navigation.findNavController(view).navigate(R.id.chatsFragment, null, navOptions);
        });

        registrationLink.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.registrationFragment);
        });
    }
}
