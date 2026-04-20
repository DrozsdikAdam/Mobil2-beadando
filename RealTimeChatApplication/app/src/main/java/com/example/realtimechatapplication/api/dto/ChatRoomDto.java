package com.example.realtimechatapplication.api.dto;

import java.util.UUID;

public class ChatRoomDto {
    private UUID id;
    private String name;
    private Boolean isGroup;
    private MessageResponseDto lastMessage;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getIsGroup() { return isGroup; }
    public void setIsGroup(Boolean group) { isGroup = group; }

    public MessageResponseDto getLastMessage() { return lastMessage; }
    public void setLastMessage(MessageResponseDto lastMessage) { this.lastMessage = lastMessage; }
}
