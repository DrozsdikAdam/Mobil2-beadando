package com.example.realtimechatbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {
    private UUID id;
    private String name;
    private Boolean isGroup;
    private MessageResponseDto lastMessage;
}
