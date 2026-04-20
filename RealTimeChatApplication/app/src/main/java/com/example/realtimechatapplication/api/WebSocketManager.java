package com.example.realtimechatapplication.api;

import android.util.Log;

import com.example.realtimechatapplication.api.dto.MessageResponseDto;
import com.example.realtimechatapplication.api.dto.SendMessageRequestDto;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private static final String WS_URL = "ws://10.0.2.2:8080/ws/websocket"; 

    private StompClient mStompClient;
    private final Gson gson = new Gson();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MessageListener messageListener;

    public interface MessageListener {
        void onMessageReceived(MessageResponseDto message);
        void onError(Throwable throwable);
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public boolean isConnected() {
        return mStompClient != null && mStompClient.isConnected();
    }

    public void connect(String authToken) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();
                    if (authToken != null) {
                        requestBuilder.addHeader("Authorization", "Bearer " + authToken);
                    }
                    return chain.proceed(requestBuilder.build());
                })
                .build();

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL, null, httpClient);

        List<StompHeader> connectHeaders = new ArrayList<>();
        if (authToken != null) {
            connectHeaders.add(new StompHeader("Authorization", "Bearer " + authToken));
        }

        mStompClient.connect(connectHeaders);

        Log.d(TAG, "Connecting to WebSocket...");

        Disposable lifecycleDisposable = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error: " + lifecycleEvent.getException().getMessage());
                            if (messageListener != null) messageListener.onError(lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.d(TAG, "Stomp connection closed");
                            break;
                    }
                }, throwable -> {
                    Log.e(TAG, "Lifecycle stream error", throwable);
                });

        compositeDisposable.add(lifecycleDisposable);
    }

    public void subscribeToChat(UUID chatRoomId) {
        if (mStompClient == null) return;

        String topic = "/topic/rooms/" + chatRoomId.toString();
        Log.d(TAG, "Subscribing to topic: " + topic);

        Disposable topicDisposable = mStompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received message: " + topicMessage.getPayload());
                    if (messageListener != null) {
                        MessageResponseDto message = gson.fromJson(topicMessage.getPayload(), MessageResponseDto.class);
                        messageListener.onMessageReceived(message);
                    }
                }, throwable -> {
                    Log.e(TAG, "Subscription error", throwable);
                });

        compositeDisposable.add(topicDisposable);
    }

    public void sendMessage(String content, UUID chatRoomId) {
        if (!isConnected()) {
            Log.e(TAG, "Cannot send message: Stomp client not connected");
            return;
        }

        SendMessageRequestDto request = new SendMessageRequestDto(content, chatRoomId);
        String jsonPayload = gson.toJson(request);

        mStompClient.send("/app/chat.sendMessage", jsonPayload)
                .subscribe(() -> {
                    Log.d(TAG, "Message sent successfully");
                }, throwable -> {
                    Log.e(TAG, "Error sending message", throwable);
                });
    }

    public void disconnect() {
        if (mStompClient != null) {
            mStompClient.disconnect();
        }
        compositeDisposable.clear();
    }
}
