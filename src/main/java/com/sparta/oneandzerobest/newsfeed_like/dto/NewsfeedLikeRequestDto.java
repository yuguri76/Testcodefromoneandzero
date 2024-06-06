package com.sparta.oneandzerobest.newsfeed_like.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 요청 DTO 클래스: NewsfeedLikeRequestDto
 * 설명: 뉴스피드 좋아요 등록/취소 요청 시 필요한 데이터를 담는 DTO 클래스입니다.
 */
@Getter
@Setter
public class NewsfeedLikeRequestDto {
    private Long userId;  // 사용자 ID
}