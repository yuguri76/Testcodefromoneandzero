package com.sparta.oneandzerobest.comment.dto;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommentRequestDtoTest {

    private final Validator validator;

    public CommentRequestDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCommentRequestDto() {
        // given
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .newsfeedId(1L)
                .content("This is a valid comment")
                .build();

        // when
        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestDto);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidCommentRequestDto_NullNewsfeedId() {
        // given
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .newsfeedId(null)
                .content("This comment has a null newsfeedId")
                .build();

        // when
        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestDto);

        // then
        assertEquals(1, violations.size());
        ConstraintViolation<CommentRequestDto> violation = violations.iterator().next();
        assertEquals("뉴스피드 ID는 필수입니다.", violation.getMessage());
    }

    @Test
    void testInvalidCommentRequestDto_BlankContent() {
        // given
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .newsfeedId(1L)
                .content("")
                .build();

        // when
        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestDto);

        // then
        assertEquals(1, violations.size());
        ConstraintViolation<CommentRequestDto> violation = violations.iterator().next();
        assertEquals("댓글 내용은 공백일 수 없습니다.", violation.getMessage());
    }

    @Test
    void testInvalidCommentRequestDto_TooLongContent() {
        // given
        String longContent = "a".repeat(256);
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .newsfeedId(1L)
                .content(longContent)
                .build();

        // when
        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestDto);

        // then
        assertEquals(1, violations.size());
        ConstraintViolation<CommentRequestDto> violation = violations.iterator().next();
        assertEquals("댓글은 최대 255자까지 입력 가능합니다.", violation.getMessage());
    }
}