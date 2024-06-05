package com.sparta.oneandzerobest.auth.service;

import com.sparta.oneandzerobest.auth.dto.TokenResponseDto;
import com.sparta.oneandzerobest.auth.entity.LoginRequest;
import com.sparta.oneandzerobest.auth.entity.LoginResponse;
import com.sparta.oneandzerobest.auth.entity.SignupRequest;

public interface UserService {
    // 회원가입
    void signup(SignupRequest signupRequest);
    // 로그인
    LoginResponse login(LoginRequest loginRequest);
    // 로그아웃
    void logout(String username);
    // 탈퇴
    void withdraw(String username, String password);
    // 리프레시 토큰
    TokenResponseDto refresh(String refreshToken);

    // 이메일 인증
    boolean verifyEmail(String username, String verificationCode);
    // 이메일 업데이트 - 이메일이 잘못된 회원가입
    void updateEmail(SignupRequest signupRequest);
    public LoginResponse loginWithOAuth(String email);
}