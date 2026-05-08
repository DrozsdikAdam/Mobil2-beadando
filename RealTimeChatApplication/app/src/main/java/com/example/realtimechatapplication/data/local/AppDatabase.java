package com.example.realtimechatapplication.data.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.realtimechatapplication.data.local.dao.ChatRoomDao;
import com.example.realtimechatapplication.data.local.dao.MessageDao;
import com.example.realtimechatapplication.data.local.entity.ChatRoomEntity;
import com.example.realtimechatapplication.data.local.entity.MessageEntity;

@Database(entities = {ChatRoomEntity.class, MessageEntity.class}, version = 1)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract ChatRoomDao chatRoomDao();
    public abstract MessageDao messageDao();

    private static volatile AppDatabase instance;

    public static AppDatabase getDatabase(final Context context){
        if (instance != null){
            return instance;
        }
        synchronized (AppDatabase.class) {
            if (instance != null){
                return instance;
            }
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "chat_database").fallbackToDestructiveMigration().build();
            return instance;
        }
    }

}
