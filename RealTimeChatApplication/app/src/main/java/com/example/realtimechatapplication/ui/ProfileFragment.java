package com.example.realtimechatapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.realtimechatapplication.R;

public class ProfileFragment extends Fragment {

    private ImageButton backButton;
    private LinearLayout profileDetailsHeader;
    private LinearLayout profileDetailsContent;
    private ImageView dropdownArrow;

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

        backButton = view.findViewById(R.id.backButton);
        profileDetailsHeader = view.findViewById(R.id.profileDetailsHeader);
        profileDetailsContent = view.findViewById(R.id.profileDetailsContent);
        dropdownArrow = view.findViewById(R.id.dropdownArrow);

        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        if (profileDetailsHeader != null) {
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
}