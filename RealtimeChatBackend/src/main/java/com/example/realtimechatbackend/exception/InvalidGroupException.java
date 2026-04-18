package com.example.realtimechatbackend.exception;

public class InvalidGroupException extends RuntimeException {
    public InvalidGroupException(String message) {
        super(message);
    }
}
