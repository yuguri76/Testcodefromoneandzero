package com.sparta.oneandzerobest.comment.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    @Test
    void testCommentCreation() {
        // given
        Long newsfeedId = 1L;
        Long userId = 2L;
        String content = "This is a comment.";

        // when
        Comment comment = new Comment(newsfeedId, userId, content);

        // then
        assertEquals(newsfeedId, comment.getNewsfeedId());
        assertEquals(userId, comment.getUserId());
        assertEquals(content, comment.getContent());
        assertNull(comment.getId());
    }

    @Test
    void testSetContent() {
        // given
        Long newsfeedId = 1L;
        Long userId = 2L;
        String initialContent = "Initial content.";
        Comment comment = new Comment(newsfeedId, userId, initialContent);

        // when
        String updatedContent = "Updated content.";
        comment.setContent(updatedContent);

        // then
        assertEquals(updatedContent, comment.getContent());
    }

    @Test
    void testSetModifiedAt() throws NoSuchFieldException, IllegalAccessException {
        // given
        Long newsfeedId = 1L;
        Long userId = 2L;
        String content = "This is a comment.";
        Comment comment = new Comment(newsfeedId, userId, content);

        // when
        LocalDateTime modifiedAt = LocalDateTime.now();
        comment.setModifiedAt(modifiedAt);

        // then
        Field field = Comment.class.getSuperclass().getDeclaredField("modifiedAt");
        field.setAccessible(true);
        assertEquals(modifiedAt, field.get(comment));
    }

    @Test
    void testCommentBuilder() {
        // given
        Long newsfeedId = 1L;
        Long userId = 2L;
        String content = "This is a comment.";

        // when
        Comment comment = Comment.builder()
                .newsfeedId(newsfeedId)
                .userId(userId)
                .content(content)
                .build();

        // then
        assertEquals(newsfeedId, comment.getNewsfeedId());
        assertEquals(userId, comment.getUserId());
        assertEquals(content, comment.getContent());
    }
}