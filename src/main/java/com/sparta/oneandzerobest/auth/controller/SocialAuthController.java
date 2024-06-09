package com.sparta.oneandzerobest.auth.controller;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.service.UserService;
import com.sparta.oneandzerobest.auth.social.github.GitHubServiceImpl;
import com.sparta.oneandzerobest.auth.social.service.GoogleServiceImpl;

import com.sparta.oneandzerobest.auth.social.service.KakaoServiceImpl;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/auth/social")
@RequiredArgsConstructor
public class SocialAuthController {

    private final KakaoServiceImpl kakaoService;
    private final GoogleServiceImpl googleService;
    private final GitHubServiceImpl gitHubService;

    private final UserService userService;
    private final JwtUtil jwtUtil;


    @GetMapping("/kakao")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) {
        // 인가 코드로 액세스 토큰 요청
        String accessToken = kakaoService.getAccessToken(code);

        // 액세스 토큰으로 사용자 정보 요청
        String userInfo = kakaoService.getUserInfo(accessToken);

        // 사용자 정보 파싱 및 저장
        User user = userService.saveOrUpdateKakaoUser(userInfo);

        // JWT 토큰 생성
        String jwtAccessToken = jwtUtil.createAccessToken(user.getUsername());
        String jwtRefreshToken = user.getRefreshToken();

        // 각 토큰을 별도의 헤더에 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtAccessToken);
        headers.set("Refresh-Token", jwtRefreshToken);
        log.info("카카오 로그인 성공");
        return new ResponseEntity<>("카카오 로그인 성공", headers, HttpStatus.OK);
    }

    @GetMapping("/google")
    public ResponseEntity<String> googleCallback(@RequestParam String code) {
        // 인가 코드로 액세스 토큰 요청
        String accessToken = googleService.getAccessToken(code);
        log.debug(accessToken);

        // 액세스 토큰으로 사용자 정보 요청
        String userInfo = googleService.getUserInfo(accessToken);
        log.debug(userInfo);

        // 사용자 정보 파싱 및 저장
        User user = userService.saveOrUpdateGoogleUser(userInfo);
        log.debug(user.toString());

        // JWT 토큰 생성
        String jwtAccessToken = jwtUtil.createAccessToken(user.getUsername());
        String jwtRefreshToken = user.getRefreshToken();

        // 각 토큰을 별도의 헤더에 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtAccessToken);
        headers.set("Refresh-Token", jwtRefreshToken);
        log.info("구글 로그인 성공");
       // return new ResponseEntity<>("구글 로그인 성공", headers, HttpStatus.OK);
        return ResponseEntity.ok("구글 로그인 성공"+jwtAccessToken + "인가"+code);
    }

    @GetMapping("/github")
    public ResponseEntity<String> githubCallback(@RequestParam String code) {
        // 인가 코드로 액세스 토큰 요청
        String accessToken = gitHubService.getAccessToken(code);

        // 액세스 토큰으로 사용자 정보 요청
        String userInfo = gitHubService.getUserInfo(accessToken);
        log.debug(userInfo);

        // 사용자 정보 파싱 및 저장
        User user = userService.saveOrUpdateGithubUser(userInfo);
        log.debug(user.toString());

        // JWT 토큰 생성
        String jwtAccessToken = jwtUtil.createAccessToken(user.getUsername());
        String jwtRefreshToken = user.getRefreshToken();

        // 각 토큰을 별도의 헤더에 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtAccessToken);
        headers.set("Refresh-Token", jwtRefreshToken);
        log.info("깃허브 로그인 성공");
         return new ResponseEntity<>("깃헙 로그인 성공", HttpStatus.OK);
    }
}

