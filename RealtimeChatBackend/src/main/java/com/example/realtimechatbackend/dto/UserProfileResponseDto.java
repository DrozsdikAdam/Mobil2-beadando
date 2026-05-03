package com.example.realtimechatbackend.dto;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserProfileResponseDto {
    private UUID id;
    private String username;
    private Boolean isOnline;
    private String profileImageUrl;
}