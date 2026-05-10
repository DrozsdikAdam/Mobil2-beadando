package com.example.realtimechatapplication.models;

import com.example.realtimechatapplication.api.dto.MessageResponseDto;
import com.example.realtimechatapplication.data.local.entity.MessageEntity;

public class MessageModel {

    private String Id;
    private String content;
    private Long timeStamp;
    private UserModel sender;
    private Boolean isDeleted;

    public MessageModel() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public UserModel getSender() {
        return sender;
    }

    public void setSender(UserModel sender) {
        this.sender = sender;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isSentBy(String currentUserId) {
        return this.sender != null && this.sender.getUserId() != null && this.sender.getUserId().equals(currentUserId);
    }

    public static MessageModel fromDto(MessageResponseDto dto, String currentUserId, String currentUsername) {
        MessageModel model = new MessageModel();
        model.setId(dto.getId().toString());
        model.setContent(dto.getContent());

        UserModel sender = new UserModel();
        sender.setUserName(dto.getSenderUsername());

        if (dto.getSenderUsername().equals(currentUsername)) {
            sender.setUserId(currentUserId);
        } else {
            sender.setUserId("other_" + dto.getSenderUsername());
        }

        model.setSender(sender);
        return model;
    }

    public static MessageModel fromEntity(MessageEntity entity, String currentUserId, String currentUsername) {
        MessageModel model = new MessageModel();
        model.setId(entity.getId().toString());
        model.setContent(entity.getContent());

        UserModel sender = new UserModel();
        sender.setUserName(entity.getSenderUsername());

        if (entity.getSenderUsername() != null && entity.getSenderUsername().equals(currentUsername)) {
            sender.setUserId(currentUserId);
        } else {
            sender.setUserId("other_" + entity.getSenderUsername());
        }

        model.setSender(sender);
        return model;
    }
}
