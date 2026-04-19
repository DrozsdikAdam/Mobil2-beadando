package com.example.realtimechatbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomResponseDto {
    private UUID chatRoomId;
    private String name;
    private Boolean isGroup;
    private List<UUID> failedToAddUsers;
}
