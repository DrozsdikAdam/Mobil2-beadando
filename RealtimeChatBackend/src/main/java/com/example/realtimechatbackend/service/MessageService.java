package com.example.realtimechatbackend.service;

import com.example.realtimechatbackend.dto.MessageResponseDto;
import com.example.realtimechatbackend.dto.SendMessageRequestDto;
import com.example.realtimechatbackend.exception.UserNotFoundException;
import com.example.realtimechatbackend.model.*;
import com.example.realtimechatbackend.repository.ChatRoomRepository;
import com.example.realtimechatbackend.repository.MessageRepository;
import com.example.realtimechatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageResponseDto sendMessage(@RequestBody SendMessageRequestDto request, String username) {

        Optional<User> currentUser = userRepository.findByUsernameAndIsDeletedFalse(username);
        if (currentUser.isEmpty()) throw new UserNotFoundException("User not found.");

        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(request.getChatRoomId());
        if (chatRoom.isEmpty()) throw new IllegalArgumentException("Chat room not found.");

        if (chatRoom.get().getUsers().contains(currentUser.get())) throw new IllegalArgumentException("You are not a member of this chat room.");

        Message message = new Message();
        message.setChatRoom(chatRoom.get());
        message.setContent(request.getContent());
        message.setSender(currentUser.get());
        message.setIsDeleted(false);
        message.setTimestamp(LocalDateTime.now());

        Message savedMessage = messageRepository.save(message);

        chatRoom.get().setLastMessage(savedMessage);
        chatRoomRepository.save(chatRoom.get());


        MessageResponseDto messageResponseDto = MessageResponseDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderUsername(message.getSender().getUsername())
                .chatRoomId(message.getChatRoom().getId())
                .timestamp(message.getTimestamp())
                .build();

        simpMessagingTemplate.convertAndSend("/group/"+request.getChatRoomId(), messageResponseDto);

        return messageResponseDto;
    }
}
