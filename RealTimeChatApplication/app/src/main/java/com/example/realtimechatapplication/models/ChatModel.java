package com.example.realtimechatapplication.models;

import java.util.UUID;

public class ChatModel {

    private UUID id;
    private String name;
    private String lastMessage;
    private String timeStamp;
    private String profileImageUrl;
    private boolean isGroup;

    public ChatModel(UUID id, String name, String lastMessage, String timeStamp, String profileImageUrl, boolean isGroup) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.profileImageUrl = profileImageUrl;
        this.isGroup = isGroup;
    }

    public UUID getId() { return id; }

    public String getName() { return name; }

    public String getLastMessage() { return lastMessage; }

    public String getTimeStamp() { return timeStamp; }

    public String getProfileImageUrl() { return profileImageUrl; }

    public boolean isGroup() { return isGroup; }
}
