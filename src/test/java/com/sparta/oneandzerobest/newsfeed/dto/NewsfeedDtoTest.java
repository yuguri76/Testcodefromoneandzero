package com.sparta.oneandzerobest.newsfeed.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewsfeedDtoTest {

    @Test
    public void testNewsfeedRequestDto() {
        // given
        String content = "This is a test content";

        // when
        NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder()
                .content(content)
                .build();

        // then
        assertEquals(content, requestDto.getContent());
    }

    @Test
    public void testNewsfeedResponseDto() {
        // given
        Long userId = 1L;
        String content = "This is a test content";

        // when
        NewsfeedResponseDto responseDto = NewsfeedResponseDto.builder()
                .userId(userId)
                .content(content)
                .createdAt(LocalDateTime.now()) // Ensure createdAt is set
                .build();

        // then
        assertEquals(userId, responseDto.getUserId());
        assertEquals(content, responseDto.getContent());
    }
}