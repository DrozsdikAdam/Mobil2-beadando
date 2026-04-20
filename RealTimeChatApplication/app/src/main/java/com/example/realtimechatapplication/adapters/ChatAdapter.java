package com.example.realtimechatapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realtimechatapplication.MainViewModel;
import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.models.ChatModel;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatModel> chatList;

    public ChatAdapter(List<ChatModel> chatList) {
        this.chatList = chatList;
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
        holder.profileImage.setImageResource(chat.getProfileImage());

        holder.itemView.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            MainViewModel mainViewModel = new ViewModelProvider(activity).get(MainViewModel.class);
            
            mainViewModel.setSelectedChatId(chat.getId().toString());
            mainViewModel.setSelectedChatPartnerName(chat.getName());

            Navigation.findNavController(v).navigate(R.id.chatScreenFragment);
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
