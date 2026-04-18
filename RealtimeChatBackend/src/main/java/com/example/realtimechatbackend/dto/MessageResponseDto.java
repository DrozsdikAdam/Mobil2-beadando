package com.example.realtimechatbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {
    private UUID id;
    private String content;
    private String senderUsername;
    private UUID chatRoomId;
    private LocalDateTime timestamp;
}
