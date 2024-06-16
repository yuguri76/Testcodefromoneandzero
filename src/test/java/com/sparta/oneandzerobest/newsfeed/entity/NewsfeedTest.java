package com.sparta.oneandzerobest.newsfeed.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NewsfeedTest {

    @Test
    public void testNewsfeedCreation() {
        // given
        Long userId = 1L;
        String content = "아 게시물내용이라고 응애";

        // when
        Newsfeed newsfeed = Newsfeed.builder()
                .userid(userId)
                .content(content)
                .build();

        // then
        assertEquals(userId, newsfeed.getUserid());
        assertEquals(content, newsfeed.getContent());
    }
}