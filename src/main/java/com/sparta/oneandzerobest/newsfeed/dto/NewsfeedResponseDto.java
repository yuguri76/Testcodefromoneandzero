package com.sparta.oneandzerobest.newsfeed.dto;

import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NewsfeedResponseDto {

    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;

    public NewsfeedResponseDto(Long id, Long userId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public NewsfeedResponseDto(Newsfeed newsfeed) {
        this.id = newsfeed.getId();
        this.userId = newsfeed.getUserid();
        this.content = newsfeed.getContent();
        this.createdAt = newsfeed.getCreatedAt();
    }
}