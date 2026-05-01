package com.example.realtimechatbackend.service;

import com.example.realtimechatbackend.dto.AuthResponseDto;
import com.example.realtimechatbackend.dto.CurrentUserProfileResponseDto;
import com.example.realtimechatbackend.dto.UpdateEmailRequestDto;
import com.example.realtimechatbackend.dto.UpdatePasswordRequestDto;
import com.example.realtimechatbackend.dto.UpdateProfileRequestDto;
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
    private final String PasswordValidatorRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";


    public List<UserProfileResponseDto> searchUsers(String query, String currentUsername){

        List<User> users = userRepository.searchUsersExcludingPrivateContacts(query, currentUsername);

        return users.stream().map(user -> userProfileDtoMapper(user)).toList();
    }

    public List<UserProfileResponseDto> getRecommendedUsers(String username){
        return userRepository.findRecommendedUsers(username)
                .stream().map(user -> userProfileDtoMapper(user)).toList();
    }

    public List<UserProfileResponseDto> getAllUsers(String currentUsername){
        return userRepository.findByUsernameNotAndIsDeletedFalse(currentUsername)
                .stream().map(user -> userProfileDtoMapper(user)).toList();
    }

    public CurrentUserProfileResponseDto getCurrentUser(String username){
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return CurrentUserProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .isOnline(user.getIsOnline()).build();
    }

    private UserProfileResponseDto userProfileDtoMapper(User user) {
        return UserProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .isOnline(user.getIsOnline())
                .build();
    }

    public AuthResponseDto updateProfile(UpdateProfileRequestDto request, String currentUsername) {
        AuthResponseDto response = null;

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            UpdatePasswordRequestDto passwordRequest = new UpdatePasswordRequestDto();
            passwordRequest.setNewPassword(request.getNewPassword());
            updatePassword(passwordRequest, currentUsername);
        }

        if (request.getNewEmail() != null && !request.getNewEmail().isBlank()) {
            UpdateEmailRequestDto emailRequest = new UpdateEmailRequestDto();
            emailRequest.setNewEmail(request.getNewEmail());
            updateEmail(emailRequest, currentUsername);
        }

        if (request.getNewUsername() != null && !request.getNewUsername().isBlank() && !request.getNewUsername().equals(currentUsername)) {
            UpdateUsernameRequestDto usernameRequest = new UpdateUsernameRequestDto();
            usernameRequest.setNewUsername(request.getNewUsername());
            response = updateUsername(usernameRequest, currentUsername);
        }

        if (response == null) {
            response = generateToken(currentUsername);
        }

        return response;
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

        if (!request.getNewPassword().matches(PasswordValidatorRegex)) {
            throw new InvalidPasswordFormatException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character!");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
    
    public AuthResponseDto generateToken(String username) {
        String token = jwtService.generateToken(username);
        return AuthResponseDto.builder().token(token).build();
    }
}
