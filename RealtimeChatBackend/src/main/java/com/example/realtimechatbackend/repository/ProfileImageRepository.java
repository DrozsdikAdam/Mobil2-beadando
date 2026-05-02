package com.example.realtimechatbackend.repository;

import com.example.realtimechatbackend.model.ProfileImage;
import com.example.realtimechatbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImage, UUID> {
    Optional<ProfileImage> findByUser(User user);
    Optional<ProfileImage> findByUserId(UUID userId);
}
