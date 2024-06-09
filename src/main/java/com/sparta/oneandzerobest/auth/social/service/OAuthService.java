package com.sparta.oneandzerobest.auth.social.service;

/**
 * OAuth 2.0인증 공통 인터페이스
 */
public interface OAuthService {

    String getAccessToken(String code);
    String getUserInfo(String accessToken);
}
