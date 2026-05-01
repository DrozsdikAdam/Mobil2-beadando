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
        AuthResponseDto response = userService.updateProfile(request, principal.getName());
        return ResponseEntity.ok(response);
    }
}
