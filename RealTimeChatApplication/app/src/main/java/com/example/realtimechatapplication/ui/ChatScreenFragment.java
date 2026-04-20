package com.example.realtimechatapplication.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realtimechatapplication.MainViewModel;
import com.example.realtimechatapplication.adapters.MessageAdapter;
import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.api.RetrofitClient;
import com.example.realtimechatapplication.api.WebSocketManager;
import com.example.realtimechatapplication.api.dto.MessageResponseDto;
import com.example.realtimechatapplication.api.dto.PageResponse;
import com.example.realtimechatapplication.models.MessageModel;
import com.example.realtimechatapplication.models.UserModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatScreenFragment extends Fragment {

    private static final String TAG = "ChatScreenFragment";

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<MessageModel> messageList;
    private EditText etMessage;
    private ImageButton btnSend;
    private ImageButton btnBack;
    private TextView tvChatName;
    private ShapeableImageView imgChatProfile;

    private MainViewModel mainViewModel;
    private WebSocketManager webSocketManager;
    private String currentUserId;
    private String currentUsername;
    private UUID currentChatRoomId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        etMessage = view.findViewById(R.id.editTextMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnBack = view.findViewById(R.id.backButton);
        tvChatName = view.findViewById(R.id.chatPartnerName);
        imgChatProfile = view.findViewById(R.id.chatPartnerImage);

        messageList = new ArrayList<>();
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(layoutManager);

        mainViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUserId = user.getUserId();
                currentUsername = user.getUserName();
                setupAdapter();
            }
        });

        mainViewModel.getSelectedChatId().observe(getViewLifecycleOwner(), chatId -> {
            if (chatId != null) {
                currentChatRoomId = UUID.fromString(chatId);
                loadMessages();
                setupWebSocket();
            }
        });

        mainViewModel.getSelectedChatPartnerName().observe(getViewLifecycleOwner(), name -> {
            if (name != null) tvChatName.setText(name);
        });

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }

        if (btnSend != null) {
            btnSend.setOnClickListener(v -> {
                String text = etMessage.getText().toString().trim();
                if (!text.isEmpty() && currentChatRoomId != null) {
                    if (webSocketManager != null && webSocketManager.isConnected()) {
                        // 1. Üzenet elküldése a WebSocketen
                        webSocketManager.sendMessage(text, currentChatRoomId);
                        
                        // 2. Optimista frissítés: Azonnal hozzáadjuk a helyi listához
                        MessageModel localMsg = new MessageModel();
                        localMsg.setContent(text);
                        UserModel me = new UserModel(currentUserId, currentUsername, "");
                        localMsg.setSender(me);
                        
                        messageList.add(localMsg);
                        if (messageAdapter != null) {
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                            recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                        }
                        
                        etMessage.setText("");
                    } else {
                        Toast.makeText(getContext(), "Nincs kapcsolat a szerverrel!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setupAdapter() {
        if (messageAdapter == null && currentUserId != null) {
            messageAdapter = new MessageAdapter(messageList, currentUserId);
            recyclerViewMessages.setAdapter(messageAdapter);
        }
    }

    private void loadMessages() {
        if (currentChatRoomId == null) return;
        
        mainViewModel.setIsLoading(true);
        RetrofitClient.getApiService().getMessages(currentChatRoomId, 0, 50).enqueue(new Callback<PageResponse<MessageResponseDto>>() {
            @Override
            public void onResponse(Call<PageResponse<MessageResponseDto>> call, Response<PageResponse<MessageResponseDto>> response) {
                mainViewModel.setIsLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    for (MessageResponseDto dto : response.body().getContent()) {
                        messageList.add(mapDtoToModel(dto));
                    }
                    // Backend returns newest first (DESC), reverse to show oldest at top, newest at bottom
                    Collections.reverse(messageList);
                    if (messageAdapter == null) setupAdapter();
                    if (messageAdapter != null) {
                        messageAdapter.notifyDataSetChanged();
                        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MessageResponseDto>> call, Throwable t) {
                mainViewModel.setIsLoading(false);
                Log.e(TAG, "Failed to load messages", t);
            }
        });
    }

    private void setupWebSocket() {
        if (webSocketManager == null) {
            webSocketManager = new WebSocketManager();
            webSocketManager.connect(RetrofitClient.getAuthToken()); 
            
            webSocketManager.setMessageListener(new WebSocketManager.MessageListener() {
                @Override
                public void onMessageReceived(MessageResponseDto dto) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            // Csak akkor adjuk hozzá, ha NEM mi küldtük (mivel mi már optimistán hozzáadtuk)
                            if (!dto.getSenderUsername().equals(currentUsername)) {
                                MessageModel model = mapDtoToModel(dto);
                                messageList.add(model);
                                if (messageAdapter != null) {
                                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                                    recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e(TAG, "WebSocket error", throwable);
                }
            });
        }
        
        if (currentChatRoomId != null) {
            webSocketManager.subscribeToChat(currentChatRoomId);
        }
    }

    private MessageModel mapDtoToModel(MessageResponseDto dto) {
        MessageModel model = new MessageModel();
        model.setId(dto.getId().toString());
        model.setContent(dto.getContent());
        
        UserModel sender = new UserModel();
        sender.setUserName(dto.getSenderUsername());
        
        if (dto.getSenderUsername().equals(currentUsername)) {
            sender.setUserId(currentUserId);
        } else {
            sender.setUserId("other_user");
        }
        
        model.setSender(sender);
        return model;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webSocketManager != null) {
            webSocketManager.disconnect();
        }
    }
}
