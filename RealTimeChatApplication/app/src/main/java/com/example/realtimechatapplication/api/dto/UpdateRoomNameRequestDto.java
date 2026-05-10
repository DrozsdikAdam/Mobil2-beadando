package com.example.realtimechatapplication.api.dto;

import com.google.gson.annotations.SerializedName;

public class UpdateRoomNameRequestDto {
    @SerializedName("newName")
    private String newName;

    public UpdateRoomNameRequestDto(String newName) {
        this.newName = newName;
    }

    public String getNewName() { return newName; }
    public void setNewName(String newName) { this.newName = newName; }
}
