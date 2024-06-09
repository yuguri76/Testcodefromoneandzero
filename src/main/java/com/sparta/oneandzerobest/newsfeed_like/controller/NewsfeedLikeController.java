package com.sparta.oneandzerobest.newsfeed_like.controller;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.newsfeed_like.dto.NewsfeedLikeRequestDto;
import com.sparta.oneandzerobest.newsfeed_like.dto.NewsfeedLikeResponseDto;
import com.sparta.oneandzerobest.newsfeed_like.service.NewsfeedLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/newsfeed")
public class NewsfeedLikeController {

    @Autowired
    private NewsfeedLikeService newsfeedLikeService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private User getUserFromToken(String token) {
        try {
            String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
            return userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/{newsfeedId}/like")
    public ResponseEntity<?> addLike(@PathVariable Long newsfeedId,
                                     @RequestHeader("Authorization") String token,
                                     @RequestHeader("Content-Type") String contentType,
                                     @RequestBody NewsfeedLikeRequestDto requestDto) {
        try {
            User user = getUserFromToken(token);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "유효하지 않은 토큰입니다."));
            }
            if (!user.getId().equals(requestDto.getUserId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "토큰의 사용자 정보와 요청된 사용자 정보가 일치하지 않습니다."));
            }

            NewsfeedLikeResponseDto responseDto = newsfeedLikeService.addLike(requestDto.getUserId(), newsfeedId);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{newsfeedId}/like")
    public ResponseEntity<?> removeLike(@PathVariable Long newsfeedId,
                                        @RequestHeader("Authorization") String token,
                                        @RequestHeader("Content-Type") String contentType,
                                        @RequestBody NewsfeedLikeRequestDto requestDto) {
        try {
            User user = getUserFromToken(token);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "유효하지 않은 토큰입니다."));
            }
            if (!user.getId().equals(requestDto.getUserId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "토큰의 사용자 정보와 요청된 사용자 정보가 일치하지 않습니다."));
            }

            NewsfeedLikeResponseDto responseDto = newsfeedLikeService.removeLike(requestDto.getUserId(), newsfeedId);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}