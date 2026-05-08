package com.example.realtimechatapplication.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "messages")
public class MessageEntity {
    @PrimaryKey
    private UUID id;
    private String content;
    private String senderUsername;
    private UUID chatRoomId;
    private String timestamp;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public UUID getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(UUID chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
