package com.sparta.oneandzerobest.newsfeed_like.repository;

import com.sparta.oneandzerobest.newsfeed_like.entity.NewsfeedLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * 리포지토리 인터페이스: NewsfeedLikeRepository
 * 설명: NewsfeedLike 엔티티에 대한 데이터베이스 작업을 처리합니다.
 */

@Repository
public interface NewsfeedLikeRepository extends JpaRepository<NewsfeedLike, Long> {

    // 특정 사용자가 특정 뉴스피드를 좋아요 했는지 확인
    Optional<NewsfeedLike> findByUserIdAndNewsfeedId(Long userId, Long newsfeedId);

    // 특정 사용자가 특정 뉴스피드에 남긴 좋아요를 삭제
    void deleteByUserIdAndNewsfeedId(Long userId, Long newsfeedId);

    // 특정 뉴스피드의 좋아요 수를 반환
    int countByNewsfeedId(Long newsfeedId);
}