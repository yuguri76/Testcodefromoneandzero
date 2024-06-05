package com.sparta.oneandzerobest.comments_like.controller;

import com.sparta.oneandzerobest.comments_like.dto.CommentLikeRequestDto;
import com.sparta.oneandzerobest.comments_like.dto.CommentLikeResponseDto;
import com.sparta.oneandzerobest.comments_like.service.CommentLikeService;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 컨트롤러 클래스: CommentLikeController
 * 설명: 댓글 좋아요 관련 API 엔드포인트를 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/newsfeed/{newsfeedId}/comment")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public CommentLikeController(CommentLikeService commentLikeService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.commentLikeService = commentLikeService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

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
     * 댓글 좋아요 수 조회
     * @param commentId 댓글 ID
     * @return 좋아요 수
     */
    @GetMapping("/{commentId}/like")
    public ResponseEntity<?> getLikesCount(@PathVariable Long commentId) {
        try {
            // 댓글의 좋아요 수 조회
            int likesCount = commentLikeService.getLikesCount(commentId);
            return ResponseEntity.ok(new CommentLikeResponseDto("좋아요 수 조회 성공", commentId, null, likesCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 댓글 좋아요 등록
     * @param commentId 댓글 ID
     * @param token JWT 토큰
     * @param contentType 콘텐츠 타입
     * @param requestDto 요청 DTO (userId를 포함)
     * @return 좋아요 등록 결과
     */
    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> addLike(@PathVariable Long commentId, @RequestHeader("Authorization") String token, @RequestHeader("Content-Type") String contentType, @RequestBody CommentLikeRequestDto requestDto) {
        try {
            // 토큰에서 사용자 정보 추출 및 검증
            User user = getUserFromToken(token);
            if (user == null || !user.getId().equals(requestDto.getUserId())) {
                throw new IllegalArgumentException("올바르지 않은 토큰입니다.");
            }
            // 좋아요 추가 서비스 호출
            CommentLikeResponseDto responseDto = commentLikeService.addLike(requestDto.getUserId(), commentId);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 댓글 좋아요 취소
     * @param commentId 댓글 ID
     * @param token JWT 토큰
     * @param contentType 콘텐츠 타입
     * @param requestDto 요청 DTO (userId를 포함)
     * @return 좋아요 취소 결과
     */
    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<?> removeLike(@PathVariable Long commentId, @RequestHeader("Authorization") String token, @RequestHeader("Content-Type") String contentType, @RequestBody CommentLikeRequestDto requestDto) {
        try {
            // 토큰에서 사용자 정보 추출 및 검증
            User user = getUserFromToken(token);
            if (user == null || !user.getId().equals(requestDto.getUserId())) {
                throw new IllegalArgumentException("올바르지 않은 토큰입니다.");
            }
            // 좋아요 취소 서비스 호출
            CommentLikeResponseDto responseDto = commentLikeService.removeLike(requestDto.getUserId(), commentId);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}