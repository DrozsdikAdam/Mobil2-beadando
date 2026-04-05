package com.example.realtimechatapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SplashFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //1 mp
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new fragment_login())
                        .commit();
            }
        }, 1000);
    }
}