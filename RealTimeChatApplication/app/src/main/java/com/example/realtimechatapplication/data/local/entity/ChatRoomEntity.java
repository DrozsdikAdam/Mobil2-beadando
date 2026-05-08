package com.example.realtimechatapplication.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "chat_rooms")
public class ChatRoomEntity {
    @PrimaryKey
    private UUID chatRoomId;
    private String name;
    private Boolean isGroup;
    private String lastMessage;
    private String lastMessageTimestamp;
    private String profileImageUrl;


    public UUID getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(UUID chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getGroup() {
        return isGroup;
    }

    public void setGroup(Boolean group) {
        isGroup = group;
    }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getLastMessageTimestamp() { return lastMessageTimestamp; }
    public void setLastMessageTimestamp(String lastMessageTimestamp) { this.lastMessageTimestamp = lastMessageTimestamp; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}

