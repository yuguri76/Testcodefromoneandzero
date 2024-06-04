package com.sparta.oneandzerobest.contents.repository;

import com.sparta.oneandzerobest.contents.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content,Long> {

}
