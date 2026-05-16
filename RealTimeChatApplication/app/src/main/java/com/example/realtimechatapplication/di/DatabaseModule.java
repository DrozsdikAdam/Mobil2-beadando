package com.example.realtimechatapplication.di;

import android.content.Context;

import androidx.room.Room;

import com.example.realtimechatapplication.data.local.AppDatabase;
import com.example.realtimechatapplication.data.local.dao.ChatRoomDao;
import com.example.realtimechatapplication.data.local.dao.MessageDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn({SingletonComponent.class})
public class DatabaseModule {
    @Singleton
    @Provides
    public AppDatabase provideAppDatabase(@ApplicationContext Context context){
        return  Room.databaseBuilder(context, AppDatabase.class, "chat_db").build();
    }
    @Singleton
    @Provides
    public ChatRoomDao provideChatRoomDao(AppDatabase database){
        return database.chatRoomDao();
    }
    @Singleton
    @Provides
    public MessageDao provideMessageDao(AppDatabase database){
        return database.messageDao();
    }

}
