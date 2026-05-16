package com.example.realtimechatapplication.di;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenManager {
    private  String authToken;
    @Inject
    public TokenManager(){}
    public void setToken(String token){
        this.authToken = token;
    }
    public String getToken() {
        return authToken;
    }
}
