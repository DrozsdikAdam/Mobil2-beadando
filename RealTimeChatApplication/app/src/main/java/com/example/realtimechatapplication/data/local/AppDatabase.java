package com.example.realtimechatapplication.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.realtimechatapplication.data.local.dao.ChatRoomDao;
import com.example.realtimechatapplication.data.local.dao.MessageDao;
import com.example.realtimechatapplication.data.local.entity.ChatRoomEntity;
import com.example.realtimechatapplication.data.local.entity.MessageEntity;

@Database(entities = {ChatRoomEntity.class, MessageEntity.class}, version = 2, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract ChatRoomDao chatRoomDao();
    public abstract MessageDao messageDao();

}
