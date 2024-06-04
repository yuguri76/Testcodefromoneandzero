package com.sparta.oneandzerobest.follow.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * FollowResponseDTO는 팔로우 응답 시 반환되는 데이터를 담는 DTO입니다.
 */
@Getter
@Setter
public class FollowResponseDTO {
    private Long followerId;
    private Long followeeId;
    private String message;
    private String username;

    /**
     * 팔로우 응답 DTO를 생성하는 생성자입니다.
     *
     * @param followerId 팔로워의 ID
     * @param username 팔로워의 사용자 이름
     */

    public FollowResponseDTO(Long followerId, String username) {
        this.followerId = followerId;
        this.username = username;
    }

    public FollowResponseDTO() {
    }
}