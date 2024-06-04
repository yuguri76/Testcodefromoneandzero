package com.sparta.oneandzerobest.auth.controller;


import com.sparta.oneandzerobest.auth.dto.LoginRequestDto;
import com.sparta.oneandzerobest.auth.dto.RefreshTokenRequestDto;
import com.sparta.oneandzerobest.auth.dto.SignupRequestDto;
import com.sparta.oneandzerobest.auth.dto.TokenResponseDto;
import com.sparta.oneandzerobest.auth.entity.LoginRequest;
import com.sparta.oneandzerobest.auth.entity.SignupRequest;
import com.sparta.oneandzerobest.auth.service.UserService;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthRestController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto signupRequestDto) {
        SignupRequest signupRequest = new SignupRequest(
                signupRequestDto.getUsername(),
                signupRequestDto.getPassword(),
                signupRequestDto.getEmail(),
                signupRequestDto.isAdmin(),
                signupRequestDto.getAdminToken()
        );
        userService.signup(signupRequest);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginRequest loginRequest = new LoginRequest(
                loginRequestDto.getUsername(),
                loginRequestDto.getPassword()
        );

        String token = userService.login(loginRequest);
        String refreshToken = jwtUtil.createRefreshToken(loginRequestDto.getUsername());

        TokenResponseDto tokenResponseDto = new TokenResponseDto(token, refreshToken);
        return ResponseEntity.ok(tokenResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {

        String refreshToken = refreshTokenRequestDto.getRefreshToken();
        String newAccessToken = jwtUtil.refreshToken(refreshToken);
        TokenResponseDto tokenResponseDto = new TokenResponseDto(newAccessToken, refreshToken);
        return ResponseEntity.ok(tokenResponseDto);
    }
}
