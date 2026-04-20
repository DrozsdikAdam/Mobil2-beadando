package com.example.realtimechatbackend.exception;

public class UserNotPartOfGroupException extends RuntimeException {
    public UserNotPartOfGroupException(String message) {
        super(message);
    }
}
