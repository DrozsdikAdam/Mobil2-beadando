package com.example.realtimechatbackend.service;

import com.example.realtimechatbackend.dto.UserProfileResponseDto;
import com.example.realtimechatbackend.exception.UserNotFoundException;
import com.example.realtimechatbackend.model.User;
import com.example.realtimechatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserProfileResponseDto> searchUsers(String query){

        List<User> users = userRepository.findByUsernameContainingIgnoreCaseAndIsDeletedFalse(query);

        return users.stream().map(user -> UserProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .isOnline(user.getIsOnline())
                .build()).toList();
    }

    public  List<UserProfileResponseDto> getRecommendedUsers(String username){
        return userRepository.findRecommendedUsers(username)
                .stream().map(user -> UserProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .isOnline(user.getIsOnline())
                .build()).toList();
    }

    public UserProfileResponseDto getCurrentUser(String username){
        
        User user = userRepository.findByUsernameAndIsDeletedFalse(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return UserProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .isOnline(user.getIsOnline()).build();
    }

}