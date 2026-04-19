package com.example.realtimechatbackend.config;

import com.example.realtimechatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final UserRepository userRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = headerAccessor.getUser();
        
        if (userPrincipal == null) {
            log.info("Received a new web socket connection (anonymous)");
            return;
        }

        String username = userPrincipal.getName();
        log.info("User Connected: {}", username);

        userRepository.findByUsernameAndIsDeletedFalse(username).ifPresent(user -> {
            user.setIsOnline(true);
            userRepository.save(user);
        });
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = headerAccessor.getUser();
        
        if (userPrincipal == null) {
            return;
        }
        
        String username = userPrincipal.getName();
        log.info("User Disconnected: {}", username);

        userRepository.findByUsernameAndIsDeletedFalse(username).ifPresent(user -> {
            user.setIsOnline(false);
            userRepository.save(user);
        });
    }
}
