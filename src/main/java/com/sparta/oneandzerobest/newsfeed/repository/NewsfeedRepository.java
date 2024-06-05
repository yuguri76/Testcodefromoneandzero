package com.sparta.oneandzerobest.newsfeed.repository;

import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NewsfeedRepository extends JpaRepository<Newsfeed,Long> {

    @Query("select n from Newsfeed n where n.createdAt between :startDate and :endDate")
    Page<Newsfeed> findAllByCreateAtBetween(@Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate, Pageable pageable);

}
