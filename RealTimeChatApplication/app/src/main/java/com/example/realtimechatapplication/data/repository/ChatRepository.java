package com.example.realtimechatapplication.data.repository;

import android.util.Log;

import com.example.realtimechatapplication.api.ApiService;
import com.example.realtimechatapplication.api.dto.ChatRoomDto;
import com.example.realtimechatapplication.api.dto.MessageResponseDto;
import com.example.realtimechatapplication.api.dto.PageResponse;
import com.example.realtimechatapplication.data.local.dao.ChatRoomDao;
import com.example.realtimechatapplication.data.local.dao.MessageDao;
import com.example.realtimechatapplication.data.local.entity.ChatRoomEntity;
import com.example.realtimechatapplication.data.local.entity.MessageEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {
    private final ChatRoomDao chatRoomDao;
    private final MessageDao messageDao;
    private final ApiService apiService;

    public ChatRepository(ChatRoomDao chatRoomDao, MessageDao messageDao, ApiService apiService) {
        this.chatRoomDao = chatRoomDao;
        this.messageDao = messageDao;
        this.apiService = apiService;
    }

    public Flowable<List<ChatRoomEntity>> getChatRooms() {
        return chatRoomDao.getAllChatRooms();
    }

    public void refreshChatRooms(){

        this.apiService.getUserRooms().enqueue(new Callback<List<ChatRoomDto>>() {
            @Override
            public void onResponse(Call<List<ChatRoomDto>> call, Response<List<ChatRoomDto>> response) {
               if (!response.isSuccessful()){
                   onFailure(call, new Throwable("No chat rooms found"));
                   return;
               }

                List<ChatRoomDto> chatRooms = response.body();
                if (chatRooms == null){
                    onFailure(call, new Throwable("No chat rooms found"));
                    return;
                }

                List<ChatRoomEntity> chatRoomEntities = new ArrayList<>();
                for (ChatRoomDto dto : chatRooms){
                    ChatRoomEntity entity = new ChatRoomEntity();
                    entity.setChatRoomId(dto.getId());
                    entity.setName(dto.getName());
                    entity.setGroup(dto.getIsGroup());
                    chatRoomEntities.add(entity);
                }

                Completable.fromAction(() -> chatRoomDao.insertAll(chatRoomEntities)).subscribeOn(Schedulers.io()).subscribe();
            }

            @Override
            public void onFailure(Call<List<ChatRoomDto>> call, Throwable t) {
                Log.e("ChatRepository", "Failed to load chat rooms: " + t.getMessage());
            }
        });

    }
    public Flowable<List<MessageEntity>> getMessages(UUID chatRoomId) {
        return messageDao.getMessagesByChatRoomId(chatRoomId);
    }

    public void syncMessages(UUID chatRoomId) {
        this.apiService.getMessages(chatRoomId, 0, 100).enqueue( new Callback<PageResponse<MessageResponseDto>> () {

            @Override
            public void onResponse(Call<PageResponse<MessageResponseDto>> call, Response<PageResponse<MessageResponseDto>> response) {

                if (!response.isSuccessful()){
                    onFailure(call, new Throwable("No messages found"));
                    return;
                }

                PageResponse<MessageResponseDto> pageResponse = response.body();
                if (pageResponse == null){
                    onFailure(call, new Throwable("No messages found"));
                    return;
                }

                List<MessageResponseDto> messageResponseDtos = pageResponse.getContent();
                if (messageResponseDtos == null){
                    onFailure(call, new Throwable("No messages found"));
                    return;
                }

                List<MessageEntity> messageEntities = new ArrayList<>();

                for (MessageResponseDto dto : messageResponseDtos) {
                    MessageEntity entity = new MessageEntity();
                    entity.setId(dto.getId());
                    entity.setContent(dto.getContent());
                    entity.setSenderUsername(dto.getSenderUsername());
                    entity.setChatRoomId(dto.getChatRoomId());
                    entity.setTimestamp(dto.getTimestamp());
                    messageEntities.add(entity);
                }

                Completable.fromAction(() -> messageDao.insertAll(messageEntities)).subscribeOn(Schedulers.io()).subscribe();
            }

            @Override
            public void onFailure(Call<PageResponse<MessageResponseDto>> call, Throwable t) {
                Log.e("ChatRepository", "Failed to sync messages: " + t.getMessage());
            }
        });
    }

    public void saveMessage( MessageResponseDto message) {
        if (message == null) return;

        MessageEntity entity = new MessageEntity();

        entity.setId(message.getId());
        entity.setContent(message.getContent());
        entity.setSenderUsername(message.getSenderUsername());
        entity.setChatRoomId(message.getChatRoomId());
        entity.setTimestamp(message.getTimestamp());

        Completable.fromAction(() -> messageDao.insertAll(Collections.singletonList(entity)))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
