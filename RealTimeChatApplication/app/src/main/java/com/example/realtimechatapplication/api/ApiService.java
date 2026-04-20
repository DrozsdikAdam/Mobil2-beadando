package com.example.realtimechatapplication.api;

import com.example.realtimechatapplication.api.dto.*;
import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @POST("/api/auth/register")
    Call<AuthResponseDto> register(@Body RegisterRequestDto request);

    @POST("/api/auth/login")
    Call<AuthResponseDto> login(@Body LoginRequestDto request);

    @POST("/api/rooms/create")
    Call<CreateRoomResponseDto> createRoom(@Body CreateRoomRequestDto request);

    @GET("/api/rooms")
    Call<List<ChatRoomDto>> getUserRooms();

    @GET("/api/messages/{chatRoomId}")
    Call<PageResponse<MessageResponseDto>> getMessages(
            @Path("chatRoomId") UUID chatRoomId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("/api/users/search")
    Call<List<UserProfileResponseDto>> searchUsers(@Query("query") String query);

    @GET("/api/users/recommended")
    Call<List<UserProfileResponseDto>> getRecommendedUsers();

    @GET("/api/users/all")
    Call<List<UserProfileResponseDto>> getAllUsers();

    @GET("/api/users/me")
    Call<UserProfileResponseDto> getCurrentUser();

    @PUT("/api/users/me")
    Call<AuthResponseDto> updateProfile(@Body UpdateProfileRequestDto request);
}
