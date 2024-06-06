package com.sparta.oneandzerobest.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 해당하는 유저를 찾을 수 없을 때
     *
     * @return : 클라이언트로 에러 코드와 메시지 반환
     */
    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<String> notFoundUserHandler() {
        return ResponseEntity.status(400).body("해당 유저를 찾을 수 없습니다.");
    }

    /**
     * 비밀번호가 맞지 않을 때
     *
     * @return : 클라이언트로 에러 코드와 메시지 반환
     */
    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<String> incorrectPasswordHandler() {
        return ResponseEntity.status(400).body("비밀번호가 맞지 않습니다.");
    }

    /**
     * 비밀번호가 형식이 맞지 않을 때
     *
     * @return : 클라이언트로 에러 코드와 메시지 반환
     */
    @ExceptionHandler(PasswordPatternException.class)
    public ResponseEntity<String> passwordPatternHandler() {
        return ResponseEntity.status(400).body("비밀번호는 최소 10자 이상이며 알파벳 대소문자, 숫자, 특수문자를 포함해야 합니다.");
    }

    /**
     * 비밀번호가 변경이 불가능 할 때
     *
     * @return : 클라이언트로 에러 코드와 메시지 반환
     */
    @ExceptionHandler(UnacceptablePasswordException.class)
    public ResponseEntity<String> unacceptablePasswordHandler() {
        return ResponseEntity.status(400).body("해당 비밀번호로 변경이 불가능합니다. (이전과 동일한 비밀번호로 변경할 수 없습니다.)");
    }

    /**
     * 해당 뉴스피드가 DB에 존재하지 않을 때
     *
     * @return : 클라이언트로 에러 코드와 메시지 반환
     */
    @ExceptionHandler(NotFoundNewsfeedException.class)
    public ResponseEntity<String> notFoundNewsfeedHandler() {
        return ResponseEntity.status(400).body("해당 뉴스피드는 존재하지 않습니다.");
    }

    /**
     * 유효하지 않은 파일일 때
     *
     * @return : 클라이언트로 에러 코드와 메시지 반환
     */
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<String> invalidFileHandler() {
        return ResponseEntity.status(400).body("유효하지 않은 파일입니다.");
    }

    @ExceptionHandler(NotFoundImageException.class)
    public ResponseEntity<String> notFoundImageHandler() {
        return ResponseEntity.status(400).body("이미지가 존재하지 않습니다.");
    }
}
