package com.sparta.oneandzerobest.auth.entity;

public enum UserStatus {
    UNVERIFIED("인증 전"),
    ACTIVE("정상"),
    WITHDRAWN("탈퇴");

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }
}
