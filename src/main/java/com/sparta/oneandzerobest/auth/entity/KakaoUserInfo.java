package com.sparta.oneandzerobest.auth.entity;

public class KakaoUserInfo {
    private String id;
    private String email;

    public KakaoUserInfo(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
