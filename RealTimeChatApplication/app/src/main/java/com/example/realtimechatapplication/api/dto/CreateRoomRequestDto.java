package com.example.realtimechatapplication.api.dto;

import java.util.List;
import java.util.UUID;

public class CreateRoomRequestDto {
    private String name;
    private Boolean isGroup;
    private List<UUID> userIds;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getIsGroup() { return isGroup; }
    public void setIsGroup(Boolean group) { isGroup = group; }

    public List<UUID> getUserIds() { return userIds; }
    public void setUserIds(List<UUID> userIds) { this.userIds = userIds; }
}
