package com.example.realtimechatapplication.models;

import java.util.UUID;

public class ChatModel {

    private UUID id;
    private String name;
    private String lastMessage;
    private String timeStamp;
    private int profileImage;

    public ChatModel(UUID id, String name, String lastMessage, String timeStamp, int profileImage) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.profileImage = profileImage;
    }

    public UUID getId() { return id; }

    public String getName() { return name; }

    public String getLastMessage() { return lastMessage; }

    public String getTimeStamp() { return timeStamp; }

    public int getProfileImage() { return profileImage; }
}
