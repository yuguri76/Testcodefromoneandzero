package com.sparta.oneandzerobest.newsfeed_like.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 엔티티 클래스: NewsfeedLike
 * 설명: 뉴스피드 좋아요 정보를 저장하는 엔티티 클래스입니다.
 * 좋아요를 누른 사용자와 뉴스피드 ID를 저장합니다.
 */

@Entity
@Table(name = "NewsfeedLikes", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "newsfeed_id"})})
@Getter
@Setter
public class NewsfeedLike {

    /**
     * 엔티티 클래스: NewsfeedLike
     * 설명: 뉴스피드 좋아요 정보를 저장하는 엔티티 클래스입니다.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좋아요 ID (Primary Key)

    @Column(name = "user_id", nullable = false)
    private Long userId; // 좋아요를 누른 사용자 ID

    @Column(name = "newsfeed_id", nullable = false)
    private Long newsfeedId; // 좋아요가 눌린 뉴스피드 ID

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정일자

    // No-args constructor
    public NewsfeedLike() {}

    // All-args constructor
    public NewsfeedLike(Long userId, Long newsfeedId) {
        this.userId = userId;
        this.newsfeedId = newsfeedId;
    }
}