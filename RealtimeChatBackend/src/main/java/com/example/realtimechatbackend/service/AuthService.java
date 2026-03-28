package com.example.realtimechatbackend.service;

import com.example.realtimechatbackend.dto.AuthResponseDto;
import com.example.realtimechatbackend.dto.LoginRequestDto;
import com.example.realtimechatbackend.dto.RegisterRequestDto;
import com.example.realtimechatbackend.exception.InvalidCredentialsException;
import com.example.realtimechatbackend.exception.InvalidPasswordFormatException;
import com.example.realtimechatbackend.exception.UserAlreadyExistsException;
import com.example.realtimechatbackend.exception.UserNotFoundException;
import com.example.realtimechatbackend.model.User;
import com.example.realtimechatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDto register(RegisterRequestDto registerRequest) {
        if (userRepository.existsByUsernameAndIsDeletedFalse(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists!");
        }

        if (userRepository.existsByEmailAndIsDeletedFalse(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists!");
        }

        if (!registerRequest.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new InvalidPasswordFormatException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character!");
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        User user = new User();

        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setIsDeleted(false);
        user.setIsOnline(false);

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user.getUsername());
        return AuthResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponseDto login(LoginRequestDto loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail());

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        user.setIsOnline(true);

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user.getUsername());
        return AuthResponseDto.builder()
                .token(jwtToken)
                .build();
    }
}
