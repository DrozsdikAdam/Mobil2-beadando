package com.example.realtimechatbackend.repository;

import com.example.realtimechatbackend.model.ChatRoom;
import com.example.realtimechatbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    Set<ChatRoom> findByUsersContaining(User user);

    @Query("SELECT c FROM ChatRoom c JOIN c.users m1 JOIN c.users m2 WHERE c.isGroup = false AND m1 = :user1 AND m2 = :user2")
    Optional<ChatRoom> findPrivateRoomBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
}
