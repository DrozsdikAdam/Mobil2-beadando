package com.example.realtimechatbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "profile_images")
@Getter
@Setter
@NoArgsConstructor
public class ProfileImage {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String bucketPath;

    @Column(nullable = false)
    private String publicUrl;
}