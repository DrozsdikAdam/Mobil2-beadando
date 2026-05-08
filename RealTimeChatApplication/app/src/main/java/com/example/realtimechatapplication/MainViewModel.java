package com.example.realtimechatapplication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.realtimechatapplication.api.RetrofitClient;
import com.example.realtimechatapplication.api.dto.MessageResponseDto;
import com.example.realtimechatapplication.data.local.AppDatabase;
import com.example.realtimechatapplication.data.local.entity.ChatRoomEntity;
import com.example.realtimechatapplication.data.repository.ChatRepository;
import com.example.realtimechatapplication.models.MessageModel;
import com.example.realtimechatapplication.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    private final ChatRepository chatRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Disposable messageDisposable;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        this.chatRepository = new ChatRepository(db.chatRoomDao(), db.messageDao(), RetrofitClient.getApiService());
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

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    public LiveData<List<ChatRoomEntity>> getChatRooms() { return chatRooms; }

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
