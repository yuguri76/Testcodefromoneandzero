package com.sparta.oneandzerobest.newsfeed.repository;

import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NewsfeedRepository extends JpaRepository<Newsfeed,Long> {

    /**
     * 특정 날짜 사이에 있는 뉴스피드 검색
     * @param startDate
     * @param endDate
     * @param pageable
     * @return
     */
    @Query("select n from Newsfeed n where n.createdAt between :startDate and :endDate")
    Page<Newsfeed> findAllByCreateAtBetween(@Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate, Pageable pageable);


    @Query("select n from Newsfeed n left join n.newsfeedLikeList group by n.id order by count(1) desc ")
    Page<Newsfeed> findAllByOrderByLikesCountDesc(Pageable pageable);

}
