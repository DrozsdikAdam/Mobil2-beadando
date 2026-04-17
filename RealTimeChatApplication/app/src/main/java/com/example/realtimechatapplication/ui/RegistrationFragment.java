package com.example.realtimechatapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.realtimechatapplication.R;
import com.google.android.material.button.MaterialButton;

public class RegistrationFragment extends Fragment {

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

        MaterialButton registerButton = view.findViewById(R.id.RegisterButton);
        TextView backToLoginLink = view.findViewById(R.id.BackToLoginLink);
        View backButton = view.findViewById(R.id.backButton);

        //Regisztráció -> Chats
        registerButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.chatsFragment);
        });

        //Vissza a NavController stack-jén
        backToLoginLink.setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });

        //Vissza a NavController stack-jén
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                Navigation.findNavController(view).popBackStack();
            });
        }
    }
}
