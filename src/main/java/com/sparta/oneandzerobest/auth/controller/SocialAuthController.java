package com.sparta.oneandzerobest.auth.controller;

import com.sparta.oneandzerobest.auth.service.KakaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/social")
public class SocialAuthController {

    @Autowired
    private KakaoService kakaoService;

    @GetMapping("/kakao")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) {
        // 인가 코드를 사용하여 액세스 토큰 요청
        String accessToken = kakaoService.getAccessToken(code);

        // 액세스 토큰을 사용하여 사용자 정보 요청
        String userInfo = kakaoService.getUserInfo(accessToken);

        return ResponseEntity.ok(userInfo);
    }
}

