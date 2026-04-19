package com.example.realtimechatbackend.service;

import com.example.realtimechatbackend.dto.AuthResponseDto;
import com.example.realtimechatbackend.dto.UpdateEmailRequestDto;
import com.example.realtimechatbackend.dto.UpdatePasswordRequestDto;
import com.example.realtimechatbackend.dto.UpdateUsernameRequestDto;
import com.example.realtimechatbackend.dto.UserProfileResponseDto;
import com.example.realtimechatbackend.exception.InvalidPasswordFormatException;
import com.example.realtimechatbackend.exception.UserAlreadyExistsException;
import com.example.realtimechatbackend.exception.UserNotFoundException;
import com.example.realtimechatbackend.exception.UsernameAlreadyInUseException;
import com.example.realtimechatbackend.model.User;
import com.example.realtimechatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public List<UserProfileResponseDto> searchUsers(String query){

        List<User> users = userRepository.findByUsernameContainingIgnoreCaseAndIsDeletedFalse(query);

        return users.stream().map(user -> UserProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .isOnline(user.getIsOnline())
                .build()).toList();
    }

    public List<UserProfileResponseDto> getRecommendedUsers(String username){
        return userRepository.findRecommendedUsers(username)
                .stream().map(user -> UserProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .isOnline(user.getIsOnline())
                .build()).toList();
    }

    public UserProfileResponseDto getCurrentUser(String username){
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return UserProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .isOnline(user.getIsOnline()).build();
    }

    public AuthResponseDto updateUsername(UpdateUsernameRequestDto request, String currentUsername) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userRepository.existsByUsernameAndIsDeletedFalse(request.getNewUsername())) {
            throw new UsernameAlreadyInUseException("Username is already taken");
        }

        user.setUsername(request.getNewUsername());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());
        return AuthResponseDto.builder().token(token).build();
    }

    public void updateEmail(UpdateEmailRequestDto request, String currentUsername) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userRepository.existsByEmailAndIsDeletedFalse(request.getNewEmail())) {
            throw new UserAlreadyExistsException("Email is already taken");
        }

        user.setEmail(request.getNewEmail());
        userRepository.save(user);
    }

    public void updatePassword(UpdatePasswordRequestDto request, String currentUsername) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!request.getNewPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new InvalidPasswordFormatException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character!");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
