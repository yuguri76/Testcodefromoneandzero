package com.sparta.oneandzerobest.comments_like.service;

import com.sparta.oneandzerobest.comments_like.dto.CommentLikeResponseDto;
import com.sparta.oneandzerobest.comment.entity.Comment;
import com.sparta.oneandzerobest.comments_like.entity.CommentLike;
import com.sparta.oneandzerobest.comments_like.repository.CommentLikeRepository;
import com.sparta.oneandzerobest.comment.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 서비스 클래스: CommentLikeService
 * 설명: 댓글 좋아요 관련 비즈니스 로직을 처리합니다.
 */
@Service
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    public CommentLikeService(CommentLikeRepository commentLikeRepository, CommentRepository commentRepository) {
        this.commentLikeRepository = commentLikeRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * 댓글 좋아요 수 조회
     * @param commentId 댓글 ID
     * @return 좋아요 수
     */
    public int getLikesCount(Long commentId) {
        // 특정 댓글에 대한 좋아요 수를 리포지토리에서 조회하여 반환
        return commentLikeRepository.countByCommentId(commentId);
    }

    /**
     * 댓글 좋아요 추가
     * @param userId 사용자 ID
     * @param commentId 댓글 ID
     * @return 좋아요 추가 결과를 담은 DTO
     */
    @Transactional
    public CommentLikeResponseDto addLike(Long userId, Long commentId) {
        // 이미 좋아요를 누른 경우 예외 발생
        if (commentLikeRepository.findByUserIdAndCommentId(userId, commentId).isPresent()) {
            throw new IllegalArgumentException("이미 좋아요한 댓글입니다.");
        }

        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 본인의 댓글에 좋아요를 누를 수 없음
        if (comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 댓글에 좋아요 할 수 없습니다.");
        }

        // 좋아요 정보를 저장
        CommentLike commentLike = new CommentLike(userId, commentId);
        commentLikeRepository.save(commentLike);

        // 좋아요 수 조회
        int likesCount = commentLikeRepository.countByCommentId(commentId);
        return new CommentLikeResponseDto("성공적으로 좋아요를 등록했습니다", commentId, userId, likesCount);
    }

    /**
     * 댓글 좋아요 취소
     * @param userId 사용자 ID
     * @param commentId 댓글 ID
     * @return 좋아요 취소 결과를 담은 DTO
     */
    @Transactional
    public CommentLikeResponseDto removeLike(Long userId, Long commentId) {
        // 좋아요가 눌린 적이 없는 경우 예외 발생
        CommentLike commentLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId)
                .orElseThrow(() -> new IllegalArgumentException("이 댓글에 좋아요하지 않았습니다."));

        // 좋아요 정보 삭제
        commentLikeRepository.deleteByUserIdAndCommentId(userId, commentId);

        // 좋아요 수 조회
        int likesCount = commentLikeRepository.countByCommentId(commentId);
        return new CommentLikeResponseDto("성공적으로 좋아요를 취소했습니다", commentId, userId, likesCount);
    }
}