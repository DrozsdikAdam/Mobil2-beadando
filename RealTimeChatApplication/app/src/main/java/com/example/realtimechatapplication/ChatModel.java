package com.example.realtimechatapplication;

public class ChatModel {

    private String name;
    private String lastMessage;
    private String timeStamp;
    private int profileImage;

    public ChatModel(String name, String lastMessage, String timeStamp, int profileImage) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.profileImage = profileImage;
    }

    public String getName() { return name; }

    public String getLastMessage() { return lastMessage; }

    public String getTimeStamp() { return timeStamp; }

    public int getProfileImage() { return profileImage; }
}
