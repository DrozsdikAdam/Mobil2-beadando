package com.example.realtimechatbackend.controller;

import com.example.realtimechatbackend.dto.AuthResponseDto;
import com.example.realtimechatbackend.dto.CurrentUserProfileResponseDto;
import com.example.realtimechatbackend.dto.UpdateEmailRequestDto;
import com.example.realtimechatbackend.dto.UpdatePasswordRequestDto;
import com.example.realtimechatbackend.dto.UpdateProfileRequestDto;
import com.example.realtimechatbackend.dto.UpdateUsernameRequestDto;
import com.example.realtimechatbackend.dto.UserProfileResponseDto;
import com.example.realtimechatbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<List<UserProfileResponseDto>> searchUsers(@RequestParam String query, Principal principal){
        return    ResponseEntity.ok(userService.searchUsers(query, principal.getName()));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<UserProfileResponseDto>> getRecommendedUsers(Principal principal){
        return ResponseEntity.ok(userService.getRecommendedUsers(principal.getName()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserProfileResponseDto>> getAllUsers(Principal principal){
        return ResponseEntity.ok(userService.getAllUsers(principal.getName()));
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserProfileResponseDto> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(userService.getCurrentUser(principal.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<AuthResponseDto> updateProfile(@RequestBody UpdateProfileRequestDto request, Principal principal) {
        String currentUsername = principal.getName();
        AuthResponseDto response = null;

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            UpdatePasswordRequestDto passwordRequest = new UpdatePasswordRequestDto();
            passwordRequest.setNewPassword(request.getNewPassword());
            userService.updatePassword(passwordRequest, currentUsername);
        }

        if (request.getNewEmail() != null && !request.getNewEmail().isBlank()) {
            UpdateEmailRequestDto emailRequest = new UpdateEmailRequestDto();
            emailRequest.setNewEmail(request.getNewEmail());
            userService.updateEmail(emailRequest, currentUsername);
        }

        if (request.getNewUsername() != null && !request.getNewUsername().isBlank() && !request.getNewUsername().equals(currentUsername)) {
            UpdateUsernameRequestDto usernameRequest = new UpdateUsernameRequestDto();
            usernameRequest.setNewUsername(request.getNewUsername());
            response = userService.updateUsername(usernameRequest, currentUsername);
        }

        if (response == null && (request.getNewPassword() != null || request.getNewEmail() != null)) {
             response = AuthResponseDto.builder().token("").build();
        }

        return ResponseEntity.ok(response);
    }
}
