package com.example.realtimechatbackend.controller;

import com.example.realtimechatbackend.dto.ChatRoomResponseDto;
import com.example.realtimechatbackend.dto.CreateRoomRequestDto;
import com.example.realtimechatbackend.dto.CreateRoomResponseDto;
import com.example.realtimechatbackend.dto.UpdateGroupNameRequestDto;
import com.example.realtimechatbackend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

    @PostMapping("/{roomId}/image")
    public ResponseEntity<?> updateGroupImage(
            @PathVariable UUID roomId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        try {
            String newImageUrl = chatRoomService.updateGroupImage(roomId, file, principal.getName());
            return ResponseEntity.ok(Map.of(
                    "message", "Csoportkép sikeresen frissítve",
                    "publicUrl", newImageUrl
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Hiba történt a kép feldolgozása során."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{roomId}/name")
    public ResponseEntity<?> updateGroupName(
            @PathVariable UUID roomId,
            @RequestBody UpdateGroupNameRequestDto request,
            Principal principal) {
        try {
            ChatRoomResponseDto updatedRoom = chatRoomService.updateGroupName(roomId, request, principal.getName());
            return ResponseEntity.ok(updatedRoom);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}