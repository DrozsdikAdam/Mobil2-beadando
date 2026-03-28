package com.example.realtimechatbackend.service;

import com.example.realtimechatbackend.dto.RegisterRequestDto;
import com.example.realtimechatbackend.model.User;
import com.example.realtimechatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequestDto registerRequest) throws Exception {
        if (userRepository.existsByUsernameAndIsDeletedFalse(registerRequest.getUsername())) {
            throw new Exception("Username already exists!");
        }

        if (userRepository.existsByEmailAndIsDeletedFalse(registerRequest.getEmail())) {
            throw new Exception("Email already exists!");
        }

        if (!registerRequest.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new Exception("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character!");
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        User user = new User();

        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setIsDeleted(false);
        user.setIsOnline(false);

        userRepository.save(user);
    }
}
