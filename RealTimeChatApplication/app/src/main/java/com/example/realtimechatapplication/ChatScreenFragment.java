package com.example.realtimechatapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class ChatScreenFragment extends Fragment {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<MessageModel> messageList;
    private EditText etMessage;
    private ImageButton btnSend;
    private ImageButton btnBack;
    private TextView tvChatName;
    private ShapeableImageView imgChatProfile;

    // fix user ID
    private String currentUserId = "user123";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nézetek inicializálása
        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnBack = view.findViewById(R.id.btnBack);
        tvChatName = view.findViewById(R.id.tvChatName);
        imgChatProfile = view.findViewById(R.id.imgChatProfile);

        // Lista és Adapter beállítása
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);

        // Küldés gomb
        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
            }
        });
        
        // Teszt adatok hozzáadása a megjelenítéshez
        addTestMessages();
    }

    private void sendMessage(String text) {
        MessageModel newMessage = new MessageModel();
        newMessage.setContent(text);
        newMessage.setTimeStamp(System.currentTimeMillis());
        newMessage.setIsDeleted(false);
        
        // Küldő beállítása (saját magunk)
        UserModel currentUser = new UserModel(currentUserId, "Én", "");
        newMessage.setSender(currentUser);
        
        messageList.add(newMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
        etMessage.setText(""); // Mező ürítése
    }

    private void addTestMessages() {
        UserModel otherUser = new UserModel("other456", "Teszt Elek", "");
        
        MessageModel m1 = new MessageModel();
        m1.setContent("Szia! Hogy vagy?");
        m1.setSender(otherUser);
        m1.setIsDeleted(false);
        messageList.add(m1);

        MessageModel m2 = new MessageModel();
        m2.setContent("Szia! Jól, köszi. Te?");
        m2.setSender(new UserModel(currentUserId, "Én", ""));
        m2.setIsDeleted(false);
        messageList.add(m2);

        messageAdapter.notifyDataSetChanged();
    }
}
