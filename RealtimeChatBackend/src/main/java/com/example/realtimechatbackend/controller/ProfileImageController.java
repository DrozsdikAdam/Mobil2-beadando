package com.example.realtimechatbackend.controller;

import com.example.realtimechatbackend.model.ProfileImage;
import com.example.realtimechatbackend.service.ProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileImageController {

    private final ProfileImageService profileImageService;

    @PostMapping("/{userId}/profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @PathVariable UUID userId,
            @RequestParam("file") MultipartFile file) {
        
        try {
            ProfileImage profileImage = profileImageService.uploadAndSaveProfileImage(userId, file);
            // Visszatérünk az új profilkép URL-jével
            return ResponseEntity.ok(Map.of(
                    "message", "Profilkép sikeresen frissítve",
                    "publicUrl", profileImage.getPublicUrl()
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Hiba történt a kép feldolgozása során."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/profile-image")
    public ResponseEntity<?> getProfileImage(@PathVariable UUID userId) {
        String publicUrl = profileImageService.getProfileImageUrl(userId);
        if (publicUrl != null) {
            return ResponseEntity.ok(Map.of("publicUrl", publicUrl));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}