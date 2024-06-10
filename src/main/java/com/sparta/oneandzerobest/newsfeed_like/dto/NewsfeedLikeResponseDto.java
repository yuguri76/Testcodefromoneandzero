package com.sparta.oneandzerobest.newsfeed_like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 응답 DTO 클래스: NewsfeedLikeResponseDto
 * 설명: 뉴스피드 좋아요 등록/취소 결과를 담는 DTO 클래스입니다.
 */
@Getter
public class NewsfeedLikeResponseDto {
    private String message;  // 결과 메시지
    private Long newsfeedId;  // 뉴스피드 ID
    private Long userId;  // 사용자 ID
    private int likesCount;  // 좋아요 수

    public NewsfeedLikeResponseDto(String message, Long newsfeedId, Long userId, int likesCount) {
        this.message = message;
        this.newsfeedId = newsfeedId;
        this.userId = userId;
        this.likesCount = likesCount;
    }
}