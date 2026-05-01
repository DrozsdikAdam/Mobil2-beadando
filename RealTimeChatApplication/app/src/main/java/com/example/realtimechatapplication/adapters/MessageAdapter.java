package com.example.realtimechatapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.models.MessageModel;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    
    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;
    
    private List<MessageModel> messageList;
    private String currentUserId;

    public MessageAdapter(List<MessageModel> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new MessageViewHolder(view);
        } else if (viewType == TYPE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_recived, parent, false);
            return new MessageViewHolder(view);
        } else {
            throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messageList.get(position);
        if (message != null && message.isSentBy(currentUserId)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return messageList != null ? messageList.size() : 0;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvSenderName;
        
        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
        }
        
        void bind(MessageModel message) {
            tvMessage.setText(message.getContent());
            if (message.getSender() != null && message.getSender().getUserName() != null) {
                tvSenderName.setText(message.getSender().getUserName());
            } else {
                tvSenderName.setText("");
            }
        }
    }
}
