package com.example.realtimechatbackend.controller;

import com.example.realtimechatbackend.dto.AuthResponseDto;
import com.example.realtimechatbackend.dto.LoginRequestDto;
import com.example.realtimechatbackend.dto.RegisterRequestDto;
import com.example.realtimechatbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
