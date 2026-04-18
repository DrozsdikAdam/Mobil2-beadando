package com.example.realtimechatbackend.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateRoomRequestDto {
    private String name;
    private Boolean isGroup;
    private List<UUID> userIds;
}
