package com.example.realtimechatapplication.api.dto;

import java.util.List;
import java.util.UUID;

public class CreateRoomResponseDto {
    private UUID chatRoomId;
    private String name;
    private Boolean isGroup;
    private List<UUID> failedToAddUsers;

    public UUID getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(UUID chatRoomId) { this.chatRoomId = chatRoomId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getIsGroup() { return isGroup; }
    public void setIsGroup(Boolean group) { isGroup = group; }

    public List<UUID> getFailedToAddUsers() { return failedToAddUsers; }
    public void setFailedToAddUsers(List<UUID> failedToAddUsers) { this.failedToAddUsers = failedToAddUsers; }
}
