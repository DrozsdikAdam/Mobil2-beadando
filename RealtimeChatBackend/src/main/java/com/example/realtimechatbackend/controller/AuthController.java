package com.example.realtimechatbackend.controller;

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
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequest){
        authService.register(registerRequest);
        return ResponseEntity.ok("Registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest){
        authService.login(loginRequest);
        return ResponseEntity.ok("Logged in successfully");
    }
}
