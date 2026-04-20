package com.example.realtimechatbackend.repository;

import com.example.realtimechatbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByUsernameAndIsDeletedFalse(String username);
    Boolean existsByUsernameAndIsDeletedFalse(String username);
    Boolean existsByEmailAndIsDeletedFalse(String email);
    List<User> findByUsernameContainingIgnoreCaseAndIsDeletedFalse(String username);
    
    @Query("SELECT u FROM User u " +
            "WHERE u.username != :currentUsername AND u.isDeleted = false AND u NOT IN " +
           "(SELECT u2 FROM ChatRoom " +
            "c JOIN c.users u1 JOIN c.users u2 " +
            "WHERE c.isGroup = false AND u1.username = :currentUsername)")
    List<User> findRecommendedUsers(@Param("currentUsername") String currentUsername);
}
