package com.example.realtimechatapplication.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.realtimechatapplication.api.ApiService;
import com.example.realtimechatapplication.api.dto.CreateRoomRequestDto;
import com.example.realtimechatapplication.api.dto.CreateRoomResponseDto;
import com.example.realtimechatapplication.models.UserModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class AddContactFragment extends Fragment {

    private static final String TAG = "AddContactFragment";
    private EditText searchContact;
    private RecyclerView recyclerViewContacts;
    private MaterialButton btnAddContact;
    private ImageButton btnBack;
    private FriendSelectAdapter adapter;
    private List<UserModel> contactList;
    private MainViewModel mainViewModel;

    @Inject
    ApiService apiService;

    public AddContactFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        searchContact = view.findViewById(R.id.searchContact);
        recyclerViewContacts = view.findViewById(R.id.recyclerViewContacts);
        btnAddContact = view.findViewById(R.id.btnAddContact);
        btnBack = view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }

        contactList = new ArrayList<>();
        adapter = new FriendSelectAdapter(contactList);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewContacts.setAdapter(adapter);

        mainViewModel.getAvailableUsers().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                contactList.clear();
                contactList.addAll(users);
                adapter.notifyDataSetChanged();
            }
        });

        searchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    mainViewModel.searchUsers(s.toString());
                } else if (s.length() == 0) {
                    mainViewModel.loadRecommendedUsers();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAddContact.setOnClickListener(v -> {
            createChatWithSelected();
        });

        mainViewModel.loadRecommendedUsers();
    }

    private void createChatWithSelected() {
        List<UUID> selectedIds = new ArrayList<>();
        String name = "";
        for (UserModel user : contactList) {
            if (user.isSelected()) {
                selectedIds.add(UUID.fromString(user.getUserId()));
                name = user.getUserName();
            }
        }

        if (selectedIds.isEmpty()) {
            Toast.makeText(getContext(), "Kérlek válassz ki valakit!", Toast.LENGTH_SHORT).show();
            return;
        }

        mainViewModel.setIsLoading(true);
        CreateRoomRequestDto request = new CreateRoomRequestDto();
        request.setIsGroup(selectedIds.size() > 1);
        request.setName(name);
        request.setUserIds(selectedIds);

        apiService.createRoom(request).enqueue(new Callback<CreateRoomResponseDto>() {
            @Override
            public void onResponse(Call<CreateRoomResponseDto> call, Response<CreateRoomResponseDto> response) {
                mainViewModel.setIsLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Chat létrehozva!", Toast.LENGTH_SHORT).show();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<CreateRoomResponseDto> call, Throwable t) {
                mainViewModel.setIsLoading(false);
                Toast.makeText(getContext(), "Hiba a létrehozáskor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
