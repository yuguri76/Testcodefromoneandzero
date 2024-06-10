package com.sparta.oneandzerobest.follow.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * FollowRequestDTO는 팔로우 요청 시 필요한 데이터를 담는 DTO입니다.
 */
@Getter
@Setter
public class FollowRequestDTO {
    private Long followeeId; // 팔로우할 사용자의 ID
}