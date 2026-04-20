package com.example.realtimechatapplication.api.dto;

import java.util.UUID;

public class SendMessageRequestDto {
    private String content;
    private UUID chatRoomId;

    public SendMessageRequestDto() {}

    public SendMessageRequestDto(String content, UUID chatRoomId) {
        this.content = content;
        this.chatRoomId = chatRoomId;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public UUID getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(UUID chatRoomId) { this.chatRoomId = chatRoomId; }
}
