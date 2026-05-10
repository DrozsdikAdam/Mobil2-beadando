package com.example.realtimechatapplication.api;

import com.example.realtimechatapplication.api.dto.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import okhttp3.MultipartBody;
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

        @PUT("/api/rooms/{roomId}/name")
        Call<ChatRoomDto> updateRoomName(
                        @Path("roomId") UUID roomId,
                        @Body UpdateRoomNameRequestDto request);

        @GET("/api/messages/{chatRoomId}")
        Call<PageResponse<MessageResponseDto>> getMessages(
                        @Path("chatRoomId") UUID chatRoomId,
                        @Query("page") int page,
                        @Query("size") int size);

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

        @Multipart
        @POST("/api/users/{userId}/profile-image")
        Call<Map<String, String>> uploadProfileImage(
                        @Path("userId") UUID userId,
                        @Part MultipartBody.Part file);

        @GET("/api/users/{userId}/profile-image")
        Call<Map<String, String>> getProfileImage(@Path("userId") UUID userId);

        @Multipart
        @POST("/api/rooms/{roomId}/image")
        Call<Map<String, String>> uploadRoomImage(
                        @Path("roomId") UUID roomId,
                        @Part MultipartBody.Part file);
}
