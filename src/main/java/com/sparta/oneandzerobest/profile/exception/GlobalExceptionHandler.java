package com.sparta.oneandzerobest.profile.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<String> notFoundUserHandler() {
        return ResponseEntity.status(400).body("해당 유저를 찾을 수 없습니다.");
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<String> incorrectPasswordHandler() {
        return ResponseEntity.status(400).body("비밀번호가 맞지 않습니다.");
    }

    @ExceptionHandler(PasswordPatternException.class)
    public ResponseEntity<String> passwordPatternHandler() {
        return ResponseEntity.status(400).body("비밀번호는 최소 10자 이상이며 알파벳 대소문자, 숫자, 특수문자를 포함해야 합니다.");
    }
}
