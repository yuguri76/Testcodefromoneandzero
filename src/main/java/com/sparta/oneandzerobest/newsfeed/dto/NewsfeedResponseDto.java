package com.sparta.oneandzerobest.newsfeed.dto;

import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import lombok.Getter;

@Getter
public class NewsfeedResponseDto {
    private Long userid;
    private String content;

    public NewsfeedResponseDto(Newsfeed newsfeed) {
        this.userid = newsfeed.getUserid();
        this.content = newsfeed.getContent();
    }
}
