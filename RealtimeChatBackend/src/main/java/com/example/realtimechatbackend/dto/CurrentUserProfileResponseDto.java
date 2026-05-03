package com.example.realtimechatbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CurrentUserProfileResponseDto {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private Boolean isOnline;
    private String profileImageUrl;
}