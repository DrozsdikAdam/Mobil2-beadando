package com.example.realtimechatapplication.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.realtimechatapplication.data.local.entity.ChatRoomEntity;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ChatRoomDao {
    @Query("SELECT * FROM chat_rooms")
    Flowable<List<ChatRoomEntity>> getAllChatRooms();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ChatRoomEntity> chatRooms);
}
