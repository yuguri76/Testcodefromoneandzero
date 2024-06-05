package com.sparta.oneandzerobest.newsfeed_like.controller;

import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.newsfeed_like.dto.NewsfeedLikeRequestDto;
import com.sparta.oneandzerobest.newsfeed_like.dto.NewsfeedLikeResponseDto;
import com.sparta.oneandzerobest.newsfeed_like.service.NewsfeedLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 컨트롤러 클래스: NewsfeedLikeController
 * 설명: 뉴스피드 좋아요 관련 API를 제공하는 컨트롤러 클래스입니다.
 */

@RestController
@RequestMapping("/newsfeed")
public class NewsfeedLikeController {

    @Autowired
    private NewsfeedLikeService newsfeedLikeService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    /**
     * JWT 토큰을 통해 사용자 정보를 가져오는 메서드
     * @param token JWT 토큰
     * @return 사용자 객체
     */
    private User getUserFromToken(String token) {
        // JWT 토큰에서 사용자 이름을 추출하고, 사용자 리포지토리에서 사용자 정보를 조회
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * 게시물 좋아요 수 조회
     * @param newsfeedId 뉴스피드 ID
     * @return 좋아요 수
     */
    @GetMapping("/{newsfeedId}/like/count")
    public ResponseEntity<?> getLikesCount(@PathVariable Long newsfeedId) {
        try {
            // 뉴스피드의 좋아요 수 조회
            int likesCount = newsfeedLikeService.getLikesCount(newsfeedId);
            return ResponseEntity.ok(new NewsfeedLikeResponseDto("좋아요 수 조회 성공", newsfeedId, null, likesCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 게시물 좋아요 등록
     * @param newsfeedId 뉴스피드 ID
     * @param token JWT 토큰
     * @param contentType 콘텐츠 타입
     * @param requestDto 요청 DTO (userId를 포함)
     * @return 좋아요 등록 결과
     */
    @PostMapping("/{newsfeedId}/like")
    public ResponseEntity<?> addLike(@PathVariable Long newsfeedId, @RequestHeader("Authorization") String token, @RequestHeader("Content-Type") String contentType, @RequestBody NewsfeedLikeRequestDto requestDto) {
        try {
            // 토큰에서 사용자 정보 추출 및 검증
            User user = getUserFromToken(token);
            if (user == null || !user.getId().equals(requestDto.getUserId())) {
                throw new IllegalArgumentException("올바르지 않은 토큰입니다.");
            }

            // 좋아요 추가 서비스 호출
            NewsfeedLikeResponseDto responseDto = newsfeedLikeService.addLike(requestDto.getUserId(), newsfeedId);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 게시물 좋아요 취소
     * @param newsfeedId 뉴스피드 ID
     * @param token JWT 토큰
     * @param contentType 콘텐츠 타입
     * @param requestDto 요청 DTO (userId를 포함)
     * @return 좋아요 취소 결과
     */
    @DeleteMapping("/{newsfeedId}/like")
    public ResponseEntity<?> removeLike(@PathVariable Long newsfeedId, @RequestHeader("Authorization") String token, @RequestHeader("Content-Type") String contentType, @RequestBody NewsfeedLikeRequestDto requestDto) {
        try {
            // 토큰에서 사용자 정보 추출 및 검증
            User user = getUserFromToken(token);
            if (user == null || !user.getId().equals(requestDto.getUserId())) {
                throw new IllegalArgumentException("올바르지 않은 토큰입니다.");
            }

            // 좋아요 취소 서비스 호출
            NewsfeedLikeResponseDto responseDto = newsfeedLikeService.removeLike(requestDto.getUserId(), newsfeedId);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}