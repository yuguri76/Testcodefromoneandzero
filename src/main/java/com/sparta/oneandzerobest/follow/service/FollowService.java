package com.sparta.oneandzerobest.follow.service;

import com.sparta.oneandzerobest.follow.dto.FollowResponseDTO;
import com.sparta.oneandzerobest.follow.entity.Follow;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.follow.repository.FollowRepository;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * FollowService는 팔로우와 언팔로우 기능을 제공하는 서비스입니다.
 * 이 서비스는 사용자 간의 팔로우 관계를 관리합니다.
 */
@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 팔로우 기능을 수행합니다.
     * @param followerId 팔로우하는 사용자의 ID
     * @param followeeId 팔로우 당하는 사용자의 ID
     * @return 팔로우 응답 데이터를 담은 DTO
     */
    public FollowResponseDTO follow(Long followerId, Long followeeId) {

        // 팔로우하는 사용자 조회
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("팔로워를 찾을 수 없습니다."));

        // 팔로우 당하는 사용자 조회
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new RuntimeException("팔로위를 찾을 수 없습니다."));


        // 이미 팔로우한 경우 예외 발생
        if (followRepository.findByFollowerAndFollowee(follower, followee).isPresent()) {
            throw new RuntimeException("이미 팔로우한 유저입니다.");
        }

        // 새로운 팔로우 관계 생성 및 저장
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowee(followee);
        followRepository.save(follow);

        // ResponseDTO 생성 및 반환
        FollowResponseDTO responseDTO = new FollowResponseDTO();
        responseDTO.setFollowerId(follower.getId());
        responseDTO.setFolloweeId(followee.getId());
        responseDTO.setMessage("유저를 성공적으로 팔로우 했습니다.");

        return responseDTO;
    }

    /**
     * 언팔로우 기능을 수행합니다.
     * @param followerId 언팔로우하는 사용자의 ID
     * @param followeeId 언팔로우 당하는 사용자의 ID
     * @return 언팔로우 응답 데이터를 담은 DTO
     */
    public FollowResponseDTO unfollow(Long followerId, Long followeeId) {

        // 언팔로우하는 사용자 조회
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("팔로워를 찾을 수 없습니다."));

        // 언팔로우 당하는 사용자 조회
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new RuntimeException("팔로위를 찾을 수 없습니다."));

        // 팔로우 관계가 존재하지 않으면 예외 발생
        Follow follow = followRepository.findByFollowerAndFollowee(follower, followee)
                .orElseThrow(() -> new RuntimeException("팔로우하지 않은 유저를 언팔로우 할 수 없습니다."));

        // 팔로우 관계 삭제
        followRepository.delete(follow);

        // ResponseDTO 생성 및 반환
        FollowResponseDTO responseDTO = new FollowResponseDTO();
        responseDTO.setFollowerId(follower.getId());
        responseDTO.setFolloweeId(followee.getId());
        responseDTO.setMessage("유저를 성공적으로 언팔로우 했습니다.");

        return responseDTO;
    }

    /**
     * 특정 사용자의 팔로워 목록을 조회합니다.
     * @param userId 팔로우 당하는 사용자의 ID
     * @return 팔로워 목록
     */
    public List<User> getFollowers(Long userId) {
        // 팔로우 당하는 사용자 조회
        User followee = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 팔로우 관계에서 팔로워 목록 추출
        return followRepository.findByFollowee(followee)
                .stream()
                .map(Follow::getFollower)
                .collect(Collectors.toList());
    }
}