package com.example.realtimechatbackend.service;

import com.example.realtimechatbackend.dto.ChatRoomResponseDto;
import com.example.realtimechatbackend.dto.CreateRoomRequestDto;
import com.example.realtimechatbackend.dto.CreateRoomResponseDto;
import com.example.realtimechatbackend.dto.MessageResponseDto;
import com.example.realtimechatbackend.exception.InvalidGroupException;
import com.example.realtimechatbackend.exception.UserNotFoundException;
import com.example.realtimechatbackend.model.ChatRoom;
import com.example.realtimechatbackend.model.Message;
import com.example.realtimechatbackend.model.ProfileImage;
import com.example.realtimechatbackend.model.User;
import com.example.realtimechatbackend.repository.ChatRoomRepository;
import com.example.realtimechatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ProfileImageService profileImageService;
    private final ImageProcessingService imageProcessingService;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional
    public CreateRoomResponseDto createRoom(CreateRoomRequestDto request, String username) {
        User currentUser = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (Boolean.TRUE.equals(request.getIsGroup())) {
            return createGroupRoom(request, currentUser);
        }

        return createPrivateRoom(request, currentUser);

    }

    private CreateRoomResponseDto createGroupRoom(CreateRoomRequestDto request, User currentUser) {
        List<UUID> failedToAdd = new ArrayList<>();
        Set<User> members = new HashSet<>();
        members.add(currentUser);

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

    private CreateRoomResponseDto createPrivateRoom(CreateRoomRequestDto request, User currentUser) {
        UUID otherUserId = request.getUserIds().getFirst();
        User otherProfile = userRepository.findById(otherUserId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + otherUserId + " not found"));
        
        Optional<ChatRoom> existingRoom = chatRoomRepository.findPrivateRoomBetweenUsers(currentUser, otherProfile);
        if (existingRoom.isPresent()) {
             return CreateRoomResponseDto.builder()
                    .chatRoomId(existingRoom.get().getId())
                    .name(existingRoom.get().getName())
                    .isGroup(existingRoom.get().getIsGroup())
                    .failedToAddUsers(new ArrayList<>())
                    .build();
        }
        
        Set<User> members = new HashSet<>();
        members.add(currentUser);
        members.add(otherProfile);

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

    @Transactional(readOnly = true)
    public Set<ChatRoomResponseDto> getUserRooms(String username) {
        User currentUser = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Set<ChatRoom> chatRooms = chatRoomRepository.findByUsersContaining(currentUser);
        
        return chatRooms.stream().map(room -> {
            MessageResponseDto lastMsgDto = room.getLastMessage() != null ? toMessageResponseDto(room.getLastMessage()) : null;
            
            String roomName = room.getName();
            String profileImageUrl = null;

            if (!Boolean.TRUE.equals(room.getIsGroup())) {
                User otherUser = room.getUsers().stream()
                        .filter(user -> !user.getId().equals(currentUser.getId()))
                        .findFirst()
                        .orElse(null);
                
                if (otherUser != null) {
                    roomName = otherUser.getUsername();
                    
                    ProfileImage profileImage = otherUser.getProfileImage();
                    if (profileImage != null) {
                        profileImageUrl = profileImage.getPublicUrl();
                    } else {
                        profileImageUrl = profileImageService.getProfileImageUrl(otherUser.getId());
                    }
                }
            } else {
                // Ha group chat, visszaadjuk a csoport saját képét, ha van
                profileImageUrl = room.getGroupImageUrl();
            }
            
            return ChatRoomResponseDto.builder()
                    .id(room.getId())
                    .name(roomName)
                    .isGroup(room.getIsGroup())
                    .lastMessage(lastMsgDto)
                    .profileImageUrl(profileImageUrl)
                    .build();
        }).collect(Collectors.toSet());
    }
    
    @Transactional
    public String updateGroupImage(UUID roomId, MultipartFile file, String username) throws IOException {
        User currentUser = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
                
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("A chatszoba nem található"));
                
        if (!Boolean.TRUE.equals(chatRoom.getIsGroup())) {
            throw new RuntimeException("Csak csoportos beszélgetésekhez lehet képet beállítani.");
        }
        
        // Memóriacím helyett egyértelműen ID alapján vizsgáljuk, hogy a felhasználó tagja-e a csoportnak
        boolean isMember = chatRoom.getUsers().stream()
                .anyMatch(user -> user.getId().equals(currentUser.getId()));
                
        if (!isMember) {
            throw new RuntimeException("Nincs jogosultságod módosítani ezt a csoportot.");
        }

        byte[] webpBytes = imageProcessingService.processProfileImage(file);

        String fileName = "group_" + roomId.toString() + "_" + UUID.randomUUID().toString() + ".webp";

        String bucketPath = supabaseStorageService.uploadImage(webpBytes, fileName);
        String publicUrl = supabaseStorageService.getPublicUrl(bucketPath);

        String oldBucketPath = chatRoom.getGroupImageBucketPath();
        if (oldBucketPath != null && !oldBucketPath.isEmpty()) {
            supabaseStorageService.deleteImage(oldBucketPath);
        }
        
        chatRoom.setGroupImageBucketPath(bucketPath);
        chatRoom.setGroupImageUrl(publicUrl);
        chatRoomRepository.save(chatRoom);
        
        return publicUrl;
    }

    private MessageResponseDto toMessageResponseDto(Message message) {
        return MessageResponseDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderUsername(message.getSender().getUsername())
                .chatRoomId(message.getChatRoom().getId())
                .timestamp(message.getTimestamp())
                .build();
    }
}