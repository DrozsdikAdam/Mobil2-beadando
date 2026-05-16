package com.example.realtimechatapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realtimechatapplication.MainViewModel;
import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.models.ChatModel;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatModel> chatList;
    private MainViewModel mainViewModel;

    public ChatAdapter(List<ChatModel> chatList, MainViewModel mainViewModel) {
        this.chatList = chatList;
        this.mainViewModel = mainViewModel;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatModel chat = chatList.get(position);
        holder.nameText.setText(chat.getName());
        holder.lastMsgText.setText(chat.getLastMessage());
        holder.timeText.setText(chat.getTimeStamp());
        
        if (chat.getProfileImageUrl() != null && !chat.getProfileImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(chat.getProfileImageUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.ic_person);
        }

        holder.itemView.setOnClickListener(v -> {
            if (mainViewModel != null) {
                mainViewModel.setSelectedChatId(chat.getId().toString());
                mainViewModel.setSelectedChatPartnerName(chat.getName());
                mainViewModel.setSelectedChatPartnerImageUrl(chat.getProfileImageUrl());
                mainViewModel.setIsSelectedChatGroup(chat.isGroup());
                Navigation.findNavController(v).navigate(R.id.chatScreenFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, lastMsgText, timeText;
        ShapeableImageView profileImage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.Name);
            lastMsgText = itemView.findViewById(R.id.lastMessage);
            timeText = itemView.findViewById(R.id.timeStamp);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }
}
