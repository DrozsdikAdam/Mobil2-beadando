package com.example.realtimechatapplication.di;

import com.example.realtimechatapplication.api.WebSocketManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class WebSocketModule {

    @Provides
    @Singleton
    public WebSocketManager provideWebSocketManager() {
        return new WebSocketManager();
    }
}
