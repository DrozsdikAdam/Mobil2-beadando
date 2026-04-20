package com.example.realtimechatbackend.dto;

import lombok.Data;

@Data
public class UpdateProfileRequestDto {
    private String newUsername;
    private String newPassword;
    private String newEmail;
}
