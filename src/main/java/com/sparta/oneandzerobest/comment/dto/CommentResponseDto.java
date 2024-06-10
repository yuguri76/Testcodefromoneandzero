package com.sparta.oneandzerobest.comment.dto;

import com.sparta.oneandzerobest.comment.entity.Comment;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

/**
 * CommentResponseDto는 댓글 조회 응답 시 반환되는 데이터를 담는 DTO
 * 이 DTO는 댓글의 ID, 뉴스피드 ID, 작성자 ID, 내용, 생성 및 수정 시간을 포함
 */
@Getter
public class CommentResponseDto {
    private Long id;  // 댓글 ID
    private Long newsfeedId;  // 뉴스피드 ID
    private Long userId;  // 댓글 작성자 ID
    private String content;  // 댓글 내용
    private String createdAt;  // 댓글 생성 시간
    private String modifiedAt;  // 댓글 수정 시간

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.newsfeedId = comment.getNewsfeedId();
        this.userId = comment.getUserId();
        this.content = comment.getContent();
        this.createdAt = formatDateTime(comment.getCreatedAt());
        this.modifiedAt = formatDateTime(comment.getModifiedAt());
    }

    // 날짜 형식을 보기 좋게 포매팅하는 메서드
    private String formatDateTime(java.time.LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
