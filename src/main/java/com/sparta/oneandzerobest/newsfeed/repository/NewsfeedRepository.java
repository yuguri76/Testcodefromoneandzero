package com.sparta.oneandzerobest.newsfeed.repository;

import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsfeedRepository extends JpaRepository<Newsfeed,Long> {

}
