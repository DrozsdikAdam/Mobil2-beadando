package com.example.realtimechatbackend.service;

import com.example.realtimechatbackend.model.ProfileImage;
import com.example.realtimechatbackend.model.User;
import com.example.realtimechatbackend.repository.ProfileImageRepository;
import com.example.realtimechatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final ProfileImageRepository profileImageRepository;
    private final UserRepository userRepository;
    private final ImageProcessingService imageProcessingService;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional
    public ProfileImage uploadAndSaveProfileImage(UUID userId, MultipartFile file) throws IOException {
        
        // 1. Megkeressük a usert
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("A felhasználó nem található."));

        // 2. Kép átalakítása WebP formátumba
        byte[] webpBytes = imageProcessingService.processProfileImage(file);

        // 3. Generálunk egy egyedi nevet a képnek
        String fileName = "user_" + userId.toString() + "_" + UUID.randomUUID().toString() + ".webp";

        // 4. Feltöltjük a Supabase Storage-be
        String bucketPath = supabaseStorageService.uploadImage(webpBytes, fileName);
        String publicUrl = supabaseStorageService.getPublicUrl(bucketPath);

        // 5. Adatbázis (Metadata) mentés
        Optional<ProfileImage> existingImageOpt = profileImageRepository.findByUser(user);
        
        ProfileImage profileImage;
        if (existingImageOpt.isPresent()) {
            profileImage = existingImageOpt.get();
            // TODO: Itt opcióként törölhetnéd is a régi fájlt a Storage-ből
            profileImage.setBucketPath(bucketPath);
            profileImage.setPublicUrl(publicUrl);
        } else {
            profileImage = new ProfileImage();
            profileImage.setUser(user);
            profileImage.setBucketPath(bucketPath);
            profileImage.setPublicUrl(publicUrl);
        }

        return profileImageRepository.save(profileImage);
    }
    
    public String getProfileImageUrl(UUID userId) {
        return profileImageRepository.findByUserId(userId)
                .map(ProfileImage::getPublicUrl)
                .orElse(null);
    }
}