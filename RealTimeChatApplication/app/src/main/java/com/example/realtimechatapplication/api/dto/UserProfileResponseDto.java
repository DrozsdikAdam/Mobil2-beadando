package com.example.realtimechatapplication.api.dto;

import java.util.UUID;

public class UserProfileResponseDto {
    private UUID id;
    private String username;
    private Boolean isOnline;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Boolean getIsOnline() { return isOnline; }
    public void setIsOnline(Boolean online) { isOnline = online; }
}
