package com.example.realtimechatapplication.data.local;

import androidx.room.TypeConverter;

import java.util.UUID;

public class Converter {
    @TypeConverter
    public String convertUUIDToString(UUID id){
        return id.toString();
    }

    @TypeConverter
    public UUID convertStringToUUID(String id){
        return UUID.fromString(id);
    }
}
