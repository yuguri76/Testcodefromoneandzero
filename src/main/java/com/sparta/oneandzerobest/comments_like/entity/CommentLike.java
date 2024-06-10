package com.sparta.oneandzerobest.comments_like.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import com.sparta.oneandzerobest.timestamp.TimeStamp;

/**
 * 엔티티 클래스: CommentLike
 * 설명: 댓글 좋아요 정보를 저장하는 엔티티 클래스입니다.
 */
@Entity
@Table(name = "CommentLikes", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "comment_id"})})
@Getter
@Setter
public class CommentLike extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좋아요 ID (Primary Key)

    @Column(name = "user_id", nullable = false)
    private Long userId; // 좋아요를 누른 사용자 ID

    @Column(name = "comment_id", nullable = false)
    private Long commentId; // 좋아요가 눌린 댓글 ID

    // 기본 생성자
    public CommentLike() {}

    // 필수 필드만 있는 생성자
    public CommentLike(Long userId, Long commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }
}