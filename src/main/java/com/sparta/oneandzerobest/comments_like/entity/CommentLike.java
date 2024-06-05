package com.sparta.oneandzerobest.comments_like.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 엔티티 클래스: CommentLike
 * 설명: 댓글 좋아요 정보를 저장하는 엔티티 클래스입니다.
 */
@Entity
@Table(name = "CommentLikes", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "comment_id"})})
@Getter
@Setter
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좋아요 ID (Primary Key)

    @Column(name = "user_id", nullable = false)
    private Long userId; // 좋아요를 누른 사용자 ID

    @Column(name = "comment_id", nullable = false)
    private Long commentId; // 좋아요가 눌린 댓글 ID

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정일자

    // No-args constructor
    public CommentLike() {}

    // All-args constructor
    public CommentLike(Long userId, Long commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }
}