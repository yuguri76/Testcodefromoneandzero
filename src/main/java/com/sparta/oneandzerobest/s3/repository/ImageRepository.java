package com.sparta.oneandzerobest.s3.repository;

import com.sparta.oneandzerobest.s3.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image,Long> {

}
