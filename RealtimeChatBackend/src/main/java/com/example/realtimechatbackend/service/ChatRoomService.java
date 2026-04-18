package com.example.realtimechatbackend.service;

import com.example.realtimechatbackend.dto.CreateRoomRequestDto;
import com.example.realtimechatbackend.dto.CreateRoomResponseDto;
import com.example.realtimechatbackend.exception.InvalidGroupException;
import com.example.realtimechatbackend.exception.UserNotFoundException;
import com.example.realtimechatbackend.model.ChatRoom;
import com.example.realtimechatbackend.model.User;
import com.example.realtimechatbackend.repository.ChatRoomRepository;
import com.example.realtimechatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public CreateRoomResponseDto createRoom(CreateRoomRequestDto request, String username) {
        Optional<User> currentUser = userRepository.findByUsernameAndIsDeletedFalse(username);
        if (currentUser.isEmpty()) throw new UserNotFoundException("User not found");

        List<UUID> failedToAdd = new ArrayList<>();

        if (Boolean.TRUE.equals(request.getIsGroup())) {
            Set<User> members = new HashSet<>();
            members.add(currentUser.get());

            for (UUID userId : request.getUserIds()) {
                Optional<User> user = userRepository.findById(userId);
                if (user.isPresent()) {
                    members.add(user.get());
                } else {
                    failedToAdd.add(userId);
                }
            }

            if (members.size() < 2) {
                throw new InvalidGroupException("Cannot create a group without any valid members!");
            }

            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setName(request.getName());
            chatRoom.setIsGroup(request.getIsGroup());
            chatRoom.setUsers(members);

            ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

            return CreateRoomResponseDto.builder()
                    .chatRoomId(savedRoom.getId())
                    .name(savedRoom.getName())
                    .isGroup(savedRoom.getIsGroup())
                    .failedToAddUsers(failedToAdd)
                    .build();

        }

        // create private chatroom
        Optional<User> otherProfile = userRepository.findById(request.getUserIds().getFirst());
        if (otherProfile.isEmpty()) throw new UserNotFoundException("User with id " + request.getUserIds().getFirst() + " not found");
        
        Optional<ChatRoom> existingRoom = chatRoomRepository.findPrivateRoomBetweenUsers(currentUser.get(), otherProfile.get());
        if (existingRoom.isPresent()) {
             return CreateRoomResponseDto.builder()
                    .chatRoomId(existingRoom.get().getId())
                    .name(existingRoom.get().getName())
                    .isGroup(existingRoom.get().getIsGroup())
                    .failedToAddUsers(new ArrayList<>())
                    .build();
        }
        
        Set<User> members = new HashSet<>();
        members.add(currentUser.get());
        members.add(otherProfile.get());

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(request.getName());
        chatRoom.setIsGroup(request.getIsGroup());
        chatRoom.setUsers(members);
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        return CreateRoomResponseDto.builder()
                .chatRoomId(savedRoom.getId())
                .name(savedRoom.getName())
                .isGroup(savedRoom.getIsGroup())
                .failedToAddUsers(new ArrayList<>())
                .build();
    }

    public Set<ChatRoom> getUserRooms(String username) {
        Optional<User> currentUser = userRepository.findByUsernameAndIsDeletedFalse(username);
        if (currentUser.isEmpty()) throw new UserNotFoundException("User not found");

        return chatRoomRepository.findByUsersContaining(currentUser.get());
    }
}
