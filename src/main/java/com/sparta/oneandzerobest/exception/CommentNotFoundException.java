package com.sparta.oneandzerobest.exception;

/**
 * 댓글이 존재하지 않거나 접근 권한이 없는 경우 발생하는 예외
 */
public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
