package com.sparta.oneandzerobest.newsfeed_like.service;

import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import com.sparta.oneandzerobest.newsfeed.repository.NewsfeedRepository;
import com.sparta.oneandzerobest.newsfeed_like.dto.NewsfeedLikeResponseDto;
import com.sparta.oneandzerobest.newsfeed_like.entity.NewsfeedLike;
import com.sparta.oneandzerobest.newsfeed_like.repository.NewsfeedLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 서비스 클래스: NewsfeedLikeService
 * 설명: 뉴스피드 좋아요 관련 비즈니스 로직을 처리합니다.
 */

@Service
public class NewsfeedLikeService {

    @Autowired
    private NewsfeedLikeRepository newsfeedLikeRepository;

    @Autowired
    private NewsfeedRepository newsfeedRepository;

    /**
     * 좋아요 추가
     * @param userId 사용자 ID
     * @param newsfeedId 뉴스피드 ID
     * @return 좋아요 추가 결과를 담은 DTO
     */
    @Transactional
    public NewsfeedLikeResponseDto addLike(Long userId, Long newsfeedId) {
        // 이미 좋아요를 누른 경우 예외 발생
        if (newsfeedLikeRepository.findByUserIdAndNewsfeedId(userId, newsfeedId).isPresent()) {
            throw new IllegalArgumentException("이미 좋아요한 게시글입니다.");
        }

        // 뉴스피드 존재 여부 확인
        Newsfeed newsfeed = newsfeedRepository.findById(newsfeedId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        // 본인의 게시물에 좋아요를 누를 수 없음
        if (newsfeed.getUserid().equals(userId)) {
            throw new IllegalArgumentException("본인의 게시물에 좋아요 할 수 없습니다.");
        }

        // 좋아요 정보를 저장
        NewsfeedLike newsfeedLike = new NewsfeedLike(userId, newsfeed);
        newsfeedLikeRepository.save(newsfeedLike);

        // 뉴스피드에 좋아요 추가
        newsfeed.setNewsfeedLike(newsfeedLike);
        newsfeedRepository.save(newsfeed);

        // 좋아요 수 조회
        int likesCount = newsfeedLikeRepository.countByNewsfeedId(newsfeedId);
        return new NewsfeedLikeResponseDto("성공적으로 좋아요를 등록했습니다", newsfeedId, userId, likesCount);
    }

    /**
     * 좋아요 취소
     * @param userId 사용자 ID
     * @param newsfeedId 뉴스피드 ID
     * @return 좋아요 취소 결과를 담은 DTO
     */
    @Transactional
    public NewsfeedLikeResponseDto removeLike(Long userId, Long newsfeedId) {
        // 좋아요가 눌린 적이 없는 경우 예외 발생
        NewsfeedLike newsfeedLike = newsfeedLikeRepository.findByUserIdAndNewsfeedId(userId, newsfeedId)
                .orElseThrow(() -> new IllegalArgumentException("이 게시글에 좋아요하지 않았습니다."));

        // 뉴스피드 존재 여부 확인
        Newsfeed newsfeed = newsfeedRepository.findById(newsfeedId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        // 뉴스피드에서 좋아요 제거
        newsfeed.removeNewsfeedLike(newsfeedLike);
        newsfeedLikeRepository.delete(newsfeedLike);
        newsfeedRepository.save(newsfeed);

        // 좋아요 수 조회
        int likesCount = newsfeedLikeRepository.countByNewsfeedId(newsfeedId);
        return new NewsfeedLikeResponseDto("성공적으로 좋아요를 취소했습니다", newsfeedId, userId, likesCount);
    }

    /**
     * 좋아요 수 조회
     * @param newsfeedId 뉴스피드 ID
     * @return 좋아요 수
     */
    public int getLikesCount(Long newsfeedId) {
        // 특정 뉴스피드에 대한 좋아요 수를 리포지토리에서 조회하여 반환
        return newsfeedLikeRepository.countByNewsfeedId(newsfeedId);
    }
}