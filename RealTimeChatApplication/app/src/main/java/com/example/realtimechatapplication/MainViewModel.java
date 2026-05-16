package com.example.realtimechatapplication;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.realtimechatapplication.api.ApiService;
import com.example.realtimechatapplication.api.dto.ChatRoomDto;
import com.example.realtimechatapplication.api.dto.MessageResponseDto;
import com.example.realtimechatapplication.api.dto.UpdateRoomNameRequestDto;
import com.example.realtimechatapplication.api.dto.UserProfileResponseDto;
import com.example.realtimechatapplication.data.local.entity.ChatRoomEntity;
import com.example.realtimechatapplication.data.repository.ChatRepository;
import com.example.realtimechatapplication.models.MessageModel;
import com.example.realtimechatapplication.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class MainViewModel extends AndroidViewModel {
    
    private final MutableLiveData<UserModel> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> selectedChatPartnerName = new MutableLiveData<>();
    private final MutableLiveData<String> selectedChatPartnerImageUrl = new MutableLiveData<>();
    private final MutableLiveData<String> selectedChatId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSelectedChatGroup = new MutableLiveData<>(false);
    private final MutableLiveData<List<MessageModel>> currentChatMessages = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<ChatRoomEntity>> chatRooms = new MutableLiveData<>();
    private final MutableLiveData<List<UserModel>> availableUsers = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPartnerOnline = new MutableLiveData<>(false);

    private final ChatRepository chatRepository;
    private final ApiService apiService;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Disposable messageDisposable;

    @Inject
    public MainViewModel(ChatRepository chatRepository, ApiService apiService, Application application) {
        super(application);
        this.chatRepository = chatRepository;
        this.apiService = apiService;
        loadChatRooms();
    }

    private void loadChatRooms() {
        disposables.add(chatRepository
                .getChatRooms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rooms -> chatRooms.setValue(rooms), 
                          throwable -> errorMessage.setValue("Hiba a szobák betöltésekor")));
    }

    public void loadMessages(UUID chatRoomId) {
        // Reset online status when switching to a new chat
        isPartnerOnline.setValue(false);

        if (messageDisposable != null && !messageDisposable.isDisposed()) {
            disposables.remove(messageDisposable);
            messageDisposable.dispose();
        }

        messageDisposable = chatRepository.getMessages(chatRoomId)
                .subscribeOn(Schedulers.io())
                .map(entities -> {
                    List<MessageModel> models = new ArrayList<>();
                    String myId = currentUser.getValue() != null ? currentUser.getValue().getUserId() : "";
                    String myName = currentUser.getValue() != null ? currentUser.getValue().getUserName() : "";

                    for (com.example.realtimechatapplication.data.local.entity.MessageEntity entity : entities) {
                        models.add(MessageModel.fromEntity(entity, myId, myName));
                    }
                    return models;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(models -> {
                    currentChatMessages.setValue(models);
                }, throwable -> {
                    errorMessage.setValue("Hiba az üzenetek betöltésekor");
                });
        
        disposables.add(messageDisposable);
    }

    public void syncMessages(UUID chatRoomId) {
        chatRepository.syncMessages(chatRoomId);
    }

    public void saveMessage(MessageResponseDto dto){
        chatRepository.saveMessage(dto);
    }

    public void refreshRooms() {
        chatRepository.refreshChatRooms();
    }

    public void changeGroupName(UUID roomId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            errorMessage.setValue("A név nem lehet üres!");
            return;
        }

        isLoading.setValue(true);
        UpdateRoomNameRequestDto request = new UpdateRoomNameRequestDto(newName.trim());

        apiService.updateRoomName(roomId, request).enqueue(new Callback<ChatRoomDto>() {
            @Override
            public void onResponse(@NonNull Call<ChatRoomDto> call, @NonNull Response<ChatRoomDto> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    selectedChatPartnerName.setValue(newName.trim());
                    refreshRooms();
                } else {
                    errorMessage.setValue("Nem sikerült megváltoztatni a nevet (Hiba: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatRoomDto> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Hálózati hiba: " + t.getMessage());
            }
        });
    }

    public void loadRecommendedUsers() {
        apiService.getRecommendedUsers().enqueue(new Callback<List<UserProfileResponseDto>>() {
            @Override
            public void onResponse(Call<List<UserProfileResponseDto>> call, Response<List<UserProfileResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserModel> users = new ArrayList<>();
                    for (UserProfileResponseDto dto : response.body()) {
                        users.add(new UserModel(dto.getId().toString(), dto.getUsername(), dto.getProfileImageUrl()));
                    }
                    availableUsers.setValue(users);
                }
            }
            @Override
            public void onFailure(Call<List<UserProfileResponseDto>> call, Throwable t) {
                Log.e("MainViewModel", "Failed to load recommended users", t);
            }
        });
    }

    public void searchUsers(String query) {
        apiService.searchUsers(query).enqueue(new Callback<List<UserProfileResponseDto>>() {
            @Override
            public void onResponse(Call<List<UserProfileResponseDto>> call, Response<List<UserProfileResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserModel> users = new ArrayList<>();
                    for (UserProfileResponseDto dto : response.body()) {
                        users.add(new UserModel(dto.getId().toString(), dto.getUsername(), dto.getProfileImageUrl()));
                    }
                    availableUsers.setValue(users);
                }
            }
            @Override
            public void onFailure(Call<List<UserProfileResponseDto>> call, Throwable t) {
                Log.e("MainViewModel", "Search failed", t);
            }
        });
    }

    public void loadAllUsers() {
        apiService.getAllUsers().enqueue(new Callback<List<UserProfileResponseDto>>() {
            @Override
            public void onResponse(Call<List<UserProfileResponseDto>> call, Response<List<UserProfileResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserModel> users = new ArrayList<>();
                    for (UserProfileResponseDto dto : response.body()) {
                        users.add(new UserModel(dto.getId().toString(), dto.getUsername(), dto.getProfileImageUrl()));
                    }
                    availableUsers.setValue(users);
                }
            }
            @Override
            public void onFailure(Call<List<UserProfileResponseDto>> call, Throwable t) {
                Log.e("MainViewModel", "Failed to load all users", t);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    public LiveData<List<ChatRoomEntity>> getChatRooms() { return chatRooms; }

    public LiveData<List<UserModel>> getAvailableUsers() { return availableUsers; }

    public LiveData<Boolean> getIsPartnerOnline() { return isPartnerOnline; }

    public void setPartnerOnline(boolean online) { isPartnerOnline.setValue(online); }

    public LiveData<UserModel> getCurrentUser() {
        return currentUser;
    }

    public LiveData<String> getSelectedChatPartnerName() {
        return selectedChatPartnerName;
    }

    public LiveData<String> getSelectedChatPartnerImageUrl() {
        return selectedChatPartnerImageUrl;
    }

    public LiveData<String> getSelectedChatId() {
        return selectedChatId;
    }

    public LiveData<Boolean> getIsSelectedChatGroup() {
        return isSelectedChatGroup;
    }

    public LiveData<List<MessageModel>> getCurrentChatMessages() {
        return currentChatMessages;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setCurrentUser(UserModel user) {
        this.currentUser.setValue(user);
    }

    public void setSelectedChatPartnerName(String partnerName) {
        this.selectedChatPartnerName.setValue(partnerName);
    }

    public void setSelectedChatPartnerImageUrl(String imageUrl) {
        this.selectedChatPartnerImageUrl.setValue(imageUrl);
    }

    public void setSelectedChatId(String id) {
        this.selectedChatId.setValue(id);
    }

    public void setIsSelectedChatGroup(boolean isGroup) {
        this.isSelectedChatGroup.setValue(isGroup);
    }

    public void setCurrentChatMessages(List<MessageModel> messages) {
        this.currentChatMessages.setValue(messages);
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading.setValue(isLoading);
    }

    public void setErrorMessage(String message) {
        this.errorMessage.setValue(message);
    }

    public void clearError() {
        this.errorMessage.setValue(null);
    }
}
