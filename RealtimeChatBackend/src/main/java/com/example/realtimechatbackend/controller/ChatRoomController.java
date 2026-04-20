package com.example.realtimechatbackend.controller;

import com.example.realtimechatbackend.dto.ChatRoomResponseDto;
import com.example.realtimechatbackend.dto.CreateRoomRequestDto;
import com.example.realtimechatbackend.dto.CreateRoomResponseDto;
import com.example.realtimechatbackend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/create")
    public ResponseEntity<CreateRoomResponseDto> createRoom(@RequestBody CreateRoomRequestDto request, Principal principal) {
        CreateRoomResponseDto response = chatRoomService.createRoom(request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Set<ChatRoomResponseDto>> getUserRooms(Principal principal) {
        return ResponseEntity.ok(chatRoomService.getUserRooms(principal.getName()));
    }
}
