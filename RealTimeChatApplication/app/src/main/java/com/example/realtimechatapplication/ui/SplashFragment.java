package com.example.realtimechatapplication.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.realtimechatapplication.R;

public class SplashFragment extends Fragment {

    private static final String ARG_DESTINATION = "destination_id";

    public static Bundle createArgs(int destinationId) {
        Bundle args = new Bundle();
        args.putInt(ARG_DESTINATION, destinationId);
        return args;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView logo = view.findViewById(R.id.logo);
        TextView title = view.findViewById(R.id.app_title);
        ProgressBar progressBar = view.findViewById(R.id.loading_progress);

        //indítás
        startAnimations(logo, title, progressBar);

        int destinationId = R.id.loginFragment;
        if (getArguments() != null && getArguments().containsKey(ARG_DESTINATION)) {
            destinationId = getArguments().getInt(ARG_DESTINATION);
        }

        final int finalDestinationId = destinationId;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.splashFragment, true)
                        .setEnterAnim(android.R.anim.fade_in)
                        .setExitAnim(android.R.anim.fade_out)
                        .build();
                Navigation.findNavController(view).navigate(finalDestinationId, null, navOptions);
            }
        }, 2500);
    }

    private void startAnimations(View logo, View title, View progressBar) {
        logo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        title.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(500)
                .start();

        progressBar.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(1000)
                .start();
    }
}
