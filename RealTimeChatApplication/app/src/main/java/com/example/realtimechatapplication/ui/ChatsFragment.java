package com.example.realtimechatapplication.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realtimechatapplication.MainViewModel;
import com.example.realtimechatapplication.adapters.ChatAdapter;
import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.api.RetrofitClient;
import com.example.realtimechatapplication.api.dto.ChatRoomDto;
import com.example.realtimechatapplication.models.ChatModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsFragment extends Fragment {

    private static final String TAG = "ChatsFragment";
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatModel> chatList;
    private MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        recyclerView = view.findViewById(R.id.chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.chatsFragment, R.id.loginFragment)
                .build();
        
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) {
                showAddOptionsDialog();
                return true;
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        loadUserChats();
    }

    private void loadUserChats() {
        mainViewModel.setIsLoading(true);
        RetrofitClient.getApiService().getUserRooms().enqueue(new Callback<List<ChatRoomDto>>() {
            @Override
            public void onResponse(Call<List<ChatRoomDto>> call, Response<List<ChatRoomDto>> response) {
                mainViewModel.setIsLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    chatList.clear();
                    for (ChatRoomDto dto : response.body()) {
                        String lastMsg = "";
                        String timeStamp = "";
                        if (dto.getLastMessage() != null) {
                            lastMsg = dto.getLastMessage().getContent();
                            if (dto.getLastMessage().getTimestamp() != null) {
                                timeStamp = formatTimestamp(dto.getLastMessage().getTimestamp());
                            }
                        }
                        chatList.add(new ChatModel(
                                dto.getId(),
                                dto.getName(),
                                lastMsg,
                                timeStamp,
                                R.drawable.ic_person
                        ));
                    }
                    chatAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Failed to load chats: " + response.code());
                    Toast.makeText(getContext(), "Nem sikerült betölteni a beszélgetéseket", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ChatRoomDto>> call, Throwable t) {
                mainViewModel.setIsLoading(false);
                Log.e(TAG, "Network error", t);
                Toast.makeText(getContext(), "Hálózati hiba a beszélgetések betöltésekor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddOptionsDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.layout_add_options, null);
        NavController navController = Navigation.findNavController(requireView());

        view.findViewById(R.id.optionAddContact).setOnClickListener(v -> {
            dialog.dismiss();
            navController.navigate(R.id.addContactFragment);
        });

        view.findViewById(R.id.optionCreateGroup).setOnClickListener(v -> {
            dialog.dismiss();
            navController.navigate(R.id.createGroupFragment);
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private String formatTimestamp(String isoTimestamp) {
        try {
            // Backend sends ISO format like "2026-04-20T20:30:00"
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = isoFormat.parse(isoTimestamp);
            if (date != null) {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return displayFormat.format(date);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse timestamp: " + isoTimestamp, e);
        }
        return isoTimestamp;
    }
}
