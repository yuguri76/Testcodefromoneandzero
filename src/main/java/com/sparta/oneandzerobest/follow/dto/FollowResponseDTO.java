package com.sparta.oneandzerobest.follow.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * FollowResponseDTO는 팔로우 응답 시 반환되는 데이터를 담은 DTO입니다.
 */
@Getter
@Setter
public class FollowResponseDTO {
    private Long followerId;
    private Long followeeId;
    private String message;

    /**
     * 팔로우 응답 DTO를 생성하는 생성자입니다.
     *
     * @param followerId 팔로워의 ID
     * @param followeeId 팔로우 당하는 사용자의 ID
     * @param message    응답 메시지
     */
    public FollowResponseDTO(Long followerId, Long followeeId, String message) {
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.message = message;
    }

    public FollowResponseDTO(Long followerId, String message) {
        this.followerId = followerId;
        this.message = message;
    }

    public FollowResponseDTO() {
    }
}