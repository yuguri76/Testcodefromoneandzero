package com.sparta.oneandzerobest.follow.controller;

import com.sparta.oneandzerobest.follow.dto.ErrorResponseDTO;
import com.sparta.oneandzerobest.follow.dto.FollowResponseDTO;
import com.sparta.oneandzerobest.follow.service.FollowService;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * FollowController는 팔로우 및 언팔로우 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    /**
     * 팔로우 요청을 처리합니다.
     * @param token JWT 토큰
     * @param userId 팔로우 당하는 사용자의 ID
     * @return 팔로우 응답 데이터를 담은 DTO
     */
    @PostMapping
    public ResponseEntity<?> follow(@RequestHeader("Authorization") String token,
                                    @PathVariable("user_id") Long userId) {

        // JWT 토큰에서 사용자 이름 추출
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));

        // 사용자 이름으로 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 자신을 팔로우할 수 없음
        if (user.getId().equals(userId)) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("스스로를 팔로우할 수 없습니다."));
        }

        // 팔로우 서비스 호출
        try {
            FollowResponseDTO responseDTO = followService.follow(user.getId(), userId);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("이미 팔로우한 유저입니다."));
        }
    }

    /**
     * 언팔로우 요청을 처리합니다.
     * @param token JWT 토큰
     * @param userId 언팔로우 당하는 사용자의 ID
     * @return 언팔로우 응답 데이터를 담은 DTO
     */
    @DeleteMapping
    public ResponseEntity<?> unfollow(@RequestHeader("Authorization") String token,
                                      @PathVariable("user_id") Long userId) {
        // JWT 토큰에서 사용자 이름 추출
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        
        // 사용자 이름으로 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 언팔로우 서비스 호출
        try {
            FollowResponseDTO responseDTO = followService.unfollow(user.getId(), userId);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("팔로우하지 않은 유저를 언팔로우 할 수 없습니다."));
        }
    }

    /**
     * 팔로우 상태를 조회합니다.
     * @param token JWT 토큰
     * @param userId 팔로우 당하는 사용자의 ID
     * @return 팔로우 상태
     */
    @GetMapping
    public ResponseEntity<?> getFollowers(@RequestHeader("Authorization") String token,
                                          @PathVariable("user_id") Long userId) {
        // JWT 토큰에서 사용자 이름 추출
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));

        // 사용자 이름으로 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 팔로워 조회
        List<User> followers = followService.getFollowers(userId);
        List<FollowResponseDTO> followersDTO = followers.stream()
                .map(follower -> new FollowResponseDTO(follower.getId(), follower.getUsername()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(followersDTO);
    }
}