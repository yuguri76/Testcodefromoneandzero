package com.sparta.oneandzerobest.follow.repository;

import com.sparta.oneandzerobest.follow.entity.Follow;
import com.sparta.oneandzerobest.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * FollowRepository는 Follow 엔티티에 대한 데이터베이스 작업을 수행하는 리포지토리입니다.
 */
public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollower(User follower);
    List<Follow> findByFollowee(User followee);
    Optional<Follow> findByFollowerAndFollowee(User follower, User followee);
}