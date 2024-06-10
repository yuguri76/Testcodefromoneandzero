package com.sparta.oneandzerobest.comments_like.dto;

import lombok.Getter;

/**
 * 응답 DTO 클래스: CommentLikeResponseDto
 * 설명: 댓글 좋아요 등록/취소 결과를 담는 DTO 클래스입니다.
 */
@Getter
public class CommentLikeResponseDto {
    private String message;  // 결과 메시지
    private Long commentId;  // 댓글 ID
    private Long userId;  // 사용자 ID
    private int likesCount;  // 좋아요 수

    public CommentLikeResponseDto(String message, Long commentId, Long userId, int likesCount) {
        this.message = message;
        this.commentId = commentId;
        this.userId = userId;
        this.likesCount = likesCount;
    }
}