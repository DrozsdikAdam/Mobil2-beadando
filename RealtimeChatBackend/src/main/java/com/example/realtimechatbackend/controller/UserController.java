package com.example.realtimechatbackend.controller;

import com.example.realtimechatbackend.dto.UserProfileResponseDto;
import com.example.realtimechatbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<UserProfileResponseDto>> searchUsers(@RequestParam String query){
        return    ResponseEntity.ok(userService.searchUsers(query));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<UserProfileResponseDto>> getRecommendedUsers(Principal principal){
        return ResponseEntity.ok(userService.getRecommendedUsers(principal.getName()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(userService.getCurrentUser(principal.getName()));
    }
}
