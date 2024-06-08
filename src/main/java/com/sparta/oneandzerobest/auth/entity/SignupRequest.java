package com.sparta.oneandzerobest.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class SignupRequest {
    private String username;
    private String password;
    private String email;
    private boolean isAdmin;
    private String adminToken;
}
