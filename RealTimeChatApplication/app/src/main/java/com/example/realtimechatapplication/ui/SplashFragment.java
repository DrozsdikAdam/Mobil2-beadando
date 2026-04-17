package com.example.realtimechatapplication.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.realtimechatapplication.R;

public class SplashFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1 mp után navigáció a LoginFragment-re, eltávolítva a Splash-t a backstack-ről
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.splashFragment, true)
                        .build();
                Navigation.findNavController(view).navigate(R.id.loginFragment, null, navOptions);
            }
        }, 1000);
    }
}
