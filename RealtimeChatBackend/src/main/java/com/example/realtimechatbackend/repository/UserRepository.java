package com.example.realtimechatbackend.repository;

import com.example.realtimechatbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByUsernameAndIsDeletedFalse(String username);
    Boolean existsByUsernameAndIsDeletedFalse(String username);
    Boolean existsByEmailAndIsDeletedFalse(String email);
}
