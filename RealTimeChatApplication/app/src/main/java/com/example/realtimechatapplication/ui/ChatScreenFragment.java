package com.example.realtimechatapplication.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realtimechatapplication.MainViewModel;
import com.example.realtimechatapplication.adapters.MessageAdapter;
import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.api.ApiService;
import com.example.realtimechatapplication.api.WebSocketManager;
import com.example.realtimechatapplication.api.dto.MessageResponseDto;
import com.example.realtimechatapplication.di.TokenManager;
import com.example.realtimechatapplication.models.MessageModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ChatScreenFragment extends Fragment {

    private static final String TAG = "ChatScreenFragment";

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<MessageModel> messageList;
    private EditText etMessage;
    private ImageButton btnSend;
    private ImageButton btnBack;
    private TextView tvChatName;
    private TextView tvPartnerStatus;
    private ShapeableImageView imgChatProfile;

    private MainViewModel mainViewModel;
    private WebSocketManager webSocketManager;
    private String currentUserId;
    private String currentUsername;
    private UUID currentChatRoomId;
    private boolean isGroupChat = false;

    @Inject
    ApiService apiService;
    @Inject
    TokenManager tokenManager;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        uploadRoomImage(imageUri);
                    }
                }
            }
    );

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
        tvPartnerStatus = view.findViewById(R.id.tvPartnerStatus);
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
                mainViewModel.loadMessages(currentChatRoomId);
                mainViewModel.syncMessages(currentChatRoomId);
                setupWebSocket();
            }
        });

        mainViewModel.getCurrentChatMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                messageList.clear();
                messageList.addAll(messages);
                if (messageAdapter != null) {
                    messageAdapter.notifyDataSetChanged();
                    recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                }
            }
        });

        mainViewModel.getSelectedChatPartnerName().observe(getViewLifecycleOwner(), name -> {
            if (name != null) tvChatName.setText(name);
        });

        mainViewModel.getIsPartnerOnline().observe(getViewLifecycleOwner(), isOnline -> {
            updateStatusDisplay(isOnline != null && isOnline);
        });

        tvChatName.setOnClickListener(v -> {
            if (isGroupChat) {
                showChangeNameDialog();
            }
        });

        mainViewModel.getIsSelectedChatGroup().observe(getViewLifecycleOwner(), isGroup -> {
            this.isGroupChat = isGroup != null && isGroup;
            if (isGroupChat) {
                imgChatProfile.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickImageLauncher.launch(intent);
                });
            } else {
                imgChatProfile.setOnClickListener(null);
            }
            updateStatusDisplay(Boolean.TRUE.equals(mainViewModel.getIsPartnerOnline().getValue()));
        });

        mainViewModel.getSelectedChatPartnerImageUrl().observe(getViewLifecycleOwner(), imageUrl -> {
            if (imgChatProfile != null) {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .into(imgChatProfile);
                } else {
                    imgChatProfile.setImageResource(R.drawable.ic_person);
                }
            }
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
                        
                        // 2. Optimista mentés Room-ba (azonnal megjelenik a UI-on az observer miatt)
                        MessageResponseDto tempDto = new MessageResponseDto();
                        tempDto.setId(UUID.randomUUID());
                        tempDto.setContent(text);
                        tempDto.setSenderUsername(currentUsername);
                        tempDto.setChatRoomId(currentChatRoomId);
                        tempDto.setTimestamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date()));
                        
                        mainViewModel.saveMessage(tempDto);
                        etMessage.setText("");
                    } else {
                        Toast.makeText(getContext(), "Nincs kapcsolat a szerverrel!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateStatusDisplay(boolean isOnline) {
        if (tvPartnerStatus == null) return;
        
        if (isGroupChat || !isOnline) {
            tvPartnerStatus.setVisibility(View.GONE);
        } else {
            tvPartnerStatus.setVisibility(View.VISIBLE);
            tvPartnerStatus.setText("Online");
            tvPartnerStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light, null));
        }
    }

    private void showChangeNameDialog() {
        EditText input = new EditText(getContext());
        input.setText(tvChatName.getText().toString());

        new AlertDialog.Builder(requireContext())
                .setTitle("Csoportnév módosítása")
                .setView(input)
                .setPositiveButton("Mentés", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        mainViewModel.changeGroupName(currentChatRoomId, newName);
                    }
                })
                .setNegativeButton("Mégse", null)
                .show();
    }

    private void uploadRoomImage(Uri imageUri) {
        if (currentChatRoomId == null || !isGroupChat) return;

        try {
            File file = uriToFile(imageUri);
            if (file == null) return;

            mainViewModel.setIsLoading(true);
            String mimeType = requireContext().getContentResolver().getType(imageUri);
            if (mimeType == null) mimeType = "image/jpeg";
            
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            apiService.uploadRoomImage(currentChatRoomId, body)
                    .enqueue(new Callback<Map<String, String>>() {
                        @Override
                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                            mainViewModel.setIsLoading(false);
                            if (response.isSuccessful() && response.body() != null) {
                                String newUrl = response.body().get("publicUrl");
                                mainViewModel.setSelectedChatPartnerImageUrl(newUrl);
                                Toast.makeText(getContext(), "Csoportkép frissítve!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Hiba a kép feltöltésekor", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
                            mainViewModel.setIsLoading(false);
                            Log.e(TAG, "Upload failed", t);
                            Toast.makeText(getContext(), "Hálózati hiba", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (IOException e) {
            Log.e(TAG, "File error", e);
        }
    }

    private File uriToFile(Uri uri) throws IOException {
        File file = new File(requireContext().getCacheDir(), "upload_room_image.jpg");
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            if (inputStream == null) return null;
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        return file;
    }

    private void setupAdapter() {
        if (messageAdapter == null && currentUserId != null) {
            messageAdapter = new MessageAdapter(messageList, currentUserId);
            recyclerViewMessages.setAdapter(messageAdapter);
        }
    }

    private void setupWebSocket() {
        if (webSocketManager == null) {
            webSocketManager = new WebSocketManager();
            webSocketManager.connect(tokenManager.getToken());
            
            webSocketManager.setMessageListener(new WebSocketManager.MessageListener() {
                @Override
                public void onMessageReceived(MessageResponseDto dto) {
                    mainViewModel.saveMessage(dto);
                    
                    // Ha a partnertől jött üzenet, online-nak jelöljük
                    if (dto.getSenderUsername() != null && !dto.getSenderUsername().equals(currentUsername)) {
                        mainViewModel.setPartnerOnline(true);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webSocketManager != null) {
            webSocketManager.disconnect();
        }
    }
}
