package com.sparta.oneandzerobest.auth.util;

import com.sparta.oneandzerobest.auth.config.JwtConfig;
import com.sparta.oneandzerobest.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {
    private static String secretKey;
    private static long tokenExpiration;
    private static long refreshTokenExpiration;

    private static final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    private JwtUtil() {}

    public static void init(JwtConfig jwtConfig) {
        secretKey = Base64.getEncoder().encodeToString(jwtConfig.getSecretKey().getBytes());
        tokenExpiration = jwtConfig.getTokenExpiration();
        refreshTokenExpiration = jwtConfig.getRefreshTokenExpiration();
    }

    public static String createAccessToken(String username) {
        return generateToken(username, tokenExpiration);
    }

    public static String createRefreshToken(String username) {
        return generateToken(username, refreshTokenExpiration);
    }

    public static String generateToken(String username, long expiration) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, Base64.getDecoder().decode(secretKey))
                .compact();
    }

    private static String getEncodedSecretKey() {
        return secretKey;
    }

    public static Claims extractClaims(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("JWT String argument cannot be null or empty.");
        }
        try {
            return Jwts.parser()
                    .setSigningKey(Base64.getDecoder().decode(secretKey))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            throw new UnauthorizedException("Invalid token", e);
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid token", e);
        }
    }

    public static boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public static boolean validateRefreshToken(String token) {
        return validateToken(token);
    }

    public static boolean validateToken(String token) {
        try {
            extractClaims(token);
            return !isTokenBlacklisted(token);
        } catch (Exception e) {
            return false;
        }
    }

    public static void addblacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public static boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public static String refreshToken(String refreshToken) {
        if (validateRefreshToken(refreshToken)) {
            String username = getUsernameFromToken(refreshToken);
            if (!isTokenBlacklisted(refreshToken)) {
                return createAccessToken(username);
            } else {
                throw new IllegalArgumentException("Refresh token is blacklisted.");
            }
        } else {
            throw new IllegalArgumentException("Refresh token is expired or invalid.");
        }
    }

    private static boolean isTokenExpired(String token) {
        final Date expiration = extractClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public static String getUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }
}
