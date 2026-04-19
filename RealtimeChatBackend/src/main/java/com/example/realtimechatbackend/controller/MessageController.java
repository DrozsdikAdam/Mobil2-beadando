package com.example.realtimechatbackend.controller;

import com.example.realtimechatbackend.dto.MessageResponseDto;
import com.example.realtimechatbackend.dto.SendMessageRequestDto;
import com.example.realtimechatbackend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;


    @MessageMapping("/chat.sendMessage")
    public MessageResponseDto sendMessage(@Payload SendMessageRequestDto request, Principal principal) {
        return messageService.sendMessage(request, principal.getName());
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<Page<MessageResponseDto>> messages(@PathVariable UUID chatRoomId, Principal principal, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(messageService.getRoomMessages(chatRoomId, principal.getName(), pageable));
    }

}
