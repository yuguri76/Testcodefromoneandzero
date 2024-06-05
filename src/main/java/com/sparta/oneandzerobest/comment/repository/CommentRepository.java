package com.sparta.oneandzerobest.comment.repository;

import com.sparta.oneandzerobest.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * CommentRepository는 댓글 엔티티에 대한 데이터베이스 작업을 수행하는 레포지토리
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByNewsfeedId(Long newsfeedId);
    Optional<Comment> findByIdAndNewsfeedIdAndUserId(Long commentId, Long newsfeedId, Long userId);
}
