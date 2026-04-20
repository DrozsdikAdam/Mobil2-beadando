package com.example.realtimechatapplication.api.dto;

import java.util.UUID;

public class UserProfileResponseDto {
    private UUID id;
    private String username;
    private String email;
    private Boolean isOnline;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getIsOnline() { return isOnline; }
    public void setIsOnline(Boolean online) { isOnline = online; }
}
