package com.sparta.oneandzerobest.auth.service;

import com.sparta.oneandzerobest.auth.entity.LoginRequest;
import com.sparta.oneandzerobest.auth.entity.SignupRequest;

public interface UserService {
    void signup(SignupRequest signupRequest);
    String login(LoginRequest loginRequest);

}