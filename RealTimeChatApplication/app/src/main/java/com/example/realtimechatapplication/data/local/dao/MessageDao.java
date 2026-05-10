package com.example.realtimechatapplication.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.realtimechatapplication.data.local.entity.MessageEntity;

import java.util.List;
import java.util.UUID;

import io.reactivex.Flowable;

@Dao
public interface MessageDao {

    @Query("SELECT * From messages WHERE chatRoomId = :chatRoomId ORDER BY timestamp ASC")
    Flowable<List<MessageEntity>> getMessagesByChatRoomId(UUID chatRoomId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MessageEntity> messages);

}
