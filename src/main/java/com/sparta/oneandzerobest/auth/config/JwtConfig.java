package com.sparta.oneandzerobest.auth.config;

import com.sparta.oneandzerobest.auth.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * JwtConfig - jwt설정
 */
@Configuration
public class JwtConfig {

    @Value("${jwt.secret.key}")
    private String secretKey;

    // JWT 토큰 만료 시간
    @Value("${jwt.token.expiration}")
    private long tokenExpiration;

    // JWT 리프레시 토큰 만료 시간
    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpiration;

    public String getSecretKey() {
        return secretKey;
    }

    public long getTokenExpiration() {
        return tokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    @PostConstruct
    public void init() {
        JwtUtil.init(this);
    }

    // Setters for testing purposes
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setTokenExpiration(long tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
}