package com.example.realtimechatbackend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SendMessageRequestDto {
    private String content;
    private UUID chatRoomId;
}
