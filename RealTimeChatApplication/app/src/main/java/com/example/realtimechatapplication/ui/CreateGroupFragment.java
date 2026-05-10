package com.example.realtimechatapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realtimechatapplication.MainViewModel;
import com.example.realtimechatapplication.adapters.FriendSelectAdapter;
import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.api.RetrofitClient;
import com.example.realtimechatapplication.api.dto.CreateRoomRequestDto;
import com.example.realtimechatapplication.api.dto.CreateRoomResponseDto;
import com.example.realtimechatapplication.models.UserModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroupFragment extends Fragment {

    private static final String TAG = "CreateGroupFragment";
    private EditText editTextGroupName;
    private RecyclerView recyclerViewFriends;
    private MaterialButton btnCreateGroup;
    private ImageButton btnBack;
    private FriendSelectAdapter adapter;
    private List<UserModel> friendList;
    private MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        editTextGroupName = view.findViewById(R.id.editTextGroupName);
        recyclerViewFriends = view.findViewById(R.id.recyclerViewFriends);
        btnCreateGroup = view.findViewById(R.id.btnCreateGroup);
        btnBack = view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }

        friendList = new ArrayList<>();
        adapter = new FriendSelectAdapter(friendList);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFriends.setAdapter(adapter);

        mainViewModel.getAvailableUsers().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                friendList.clear();
                friendList.addAll(users);
                adapter.notifyDataSetChanged();
            }
        });

        btnCreateGroup.setOnClickListener(v -> createGroup());

        loadAvailableUsers();
    }

    private void loadAvailableUsers() {
        mainViewModel.loadAllUsers();
    }

    private void createGroup() {
        String groupName = editTextGroupName.getText().toString().trim();
        if (groupName.isEmpty()) {
            Toast.makeText(getContext(), "Kérlek adj meg egy csoportnevet!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<UUID> selectedIds = new ArrayList<>();
        for (UserModel user : friendList) {
            if (user.isSelected()) {
                selectedIds.add(UUID.fromString(user.getUserId()));
            }
        }

        if (selectedIds.isEmpty()) {
            Toast.makeText(getContext(), "Legalább egy tagot válassz ki!", Toast.LENGTH_SHORT).show();
            return;
        }

        mainViewModel.setIsLoading(true);
        CreateRoomRequestDto request = new CreateRoomRequestDto();
        request.setName(groupName);
        request.setIsGroup(true);
        request.setUserIds(selectedIds);

        RetrofitClient.getApiService().createRoom(request).enqueue(new Callback<CreateRoomResponseDto>() {
            @Override
            public void onResponse(Call<CreateRoomResponseDto> call, Response<CreateRoomResponseDto> response) {
                mainViewModel.setIsLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Csoport létrehozva: " + groupName, Toast.LENGTH_SHORT).show();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Hiba: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateRoomResponseDto> call, Throwable t) {
                mainViewModel.setIsLoading(false);
                Toast.makeText(getContext(), "Hálózati hiba a csoport létrehozásakor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
