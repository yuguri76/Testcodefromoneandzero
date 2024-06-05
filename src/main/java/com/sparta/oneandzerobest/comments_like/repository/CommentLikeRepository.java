package com.sparta.oneandzerobest.comments_like.repository;

import com.sparta.oneandzerobest.comments_like.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 리포지토리 인터페이스: CommentLikeRepository
 * 설명: CommentLike 엔티티에 대한 데이터베이스 작업을 처리합니다.
 */

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // 특정 사용자가 특정 댓글을 좋아요 했는지 확인
    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);

    // 특정 사용자가 특정 댓글에 남긴 좋아요를 삭제
    void deleteByUserIdAndCommentId(Long userId, Long commentId);

    // 특정 댓글의 좋아요 수를 반환
    int countByCommentId(Long commentId);
}