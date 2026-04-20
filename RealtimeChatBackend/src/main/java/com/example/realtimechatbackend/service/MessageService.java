package com.example.realtimechatbackend.service;

import com.example.realtimechatbackend.dto.MessageResponseDto;
import com.example.realtimechatbackend.dto.SendMessageRequestDto;
import com.example.realtimechatbackend.exception.GroupNotFoundException;
import com.example.realtimechatbackend.exception.UserNotFoundException;
import com.example.realtimechatbackend.exception.UserNotPartOfGroupException;
import com.example.realtimechatbackend.model.*;
import com.example.realtimechatbackend.repository.ChatRoomRepository;
import com.example.realtimechatbackend.repository.MessageRepository;
import com.example.realtimechatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public MessageResponseDto sendMessage(SendMessageRequestDto request, String username) {

        Optional<User> currentUser = userRepository.findByUsernameAndIsDeletedFalse(username);
        if (currentUser.isEmpty()) throw new UserNotFoundException("User not found.");

        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(request.getChatRoomId());
        if (chatRoom.isEmpty()) throw new GroupNotFoundException("Chat room not found.");

        if (!chatRoom.get().getUsers().contains(currentUser.get())) throw new UserNotPartOfGroupException("You are not a member of this chat room.");

        Message message = new Message();
        message.setChatRoom(chatRoom.get());
        message.setContent(request.getContent());
        message.setSender(currentUser.get());
        message.setTimestamp(LocalDateTime.now());
        message.setIsDeleted(false);

        Message savedMessage = messageRepository.save(message);

        chatRoom.get().setLastMessage(savedMessage);
        chatRoomRepository.save(chatRoom.get());

        MessageResponseDto messageResponseDto = MessageResponseDto.builder()
                .id(savedMessage.getId())
                .content(savedMessage.getContent())
                .senderUsername(savedMessage.getSender().getUsername())
                .chatRoomId(savedMessage.getChatRoom().getId())
                .timestamp(savedMessage.getTimestamp())
                .build();

        simpMessagingTemplate.convertAndSend("/topic/rooms/" + request.getChatRoomId(), messageResponseDto);

        return messageResponseDto;
    }

    @Transactional(readOnly = true)
    public Page<MessageResponseDto> getRoomMessages(UUID chatRoomId, String username, Pageable pageAble) {

        Optional<User> user = userRepository.findByUsernameAndIsDeletedFalse(username);
        if( user.isEmpty()) throw new UserNotFoundException("User not found.");

        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if(chatRoom.isEmpty()) throw new GroupNotFoundException("Chat room not found.");
        
        if(!chatRoom.get().getUsers().contains(user.get())) throw new UserNotPartOfGroupException("You are not a member of this chat room.");

        Page<Message> messages = messageRepository.findByChatRoomOrderByTimestampDesc(chatRoom.get(), pageAble);

        return messages.map(message -> MessageResponseDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderUsername(message.getSender().getUsername())
                .chatRoomId(message.getChatRoom().getId())
                .timestamp(message.getTimestamp())
                .build());
    }
}
