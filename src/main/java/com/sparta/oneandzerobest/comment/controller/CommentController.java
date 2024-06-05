package com.sparta.oneandzerobest.comment.controller;

import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.dto.CommentResponseDto;
import com.sparta.oneandzerobest.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CommentController는 뉴스피드의 댓글 관련 요청을 처리
 * 댓글의 생성, 조회, 수정, 삭제 기능
 */
@RestController
@RequestMapping("/newsfeed/{newsfeedId}/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 댓글을 생성
     * @param newsfeedId 댓글이 작성될 뉴스피드 ID
     * @param requestDto 댓글 작성 데이터
     * @param token 사용자 인증 토큰
     * @return 생성된 댓글 정보 또는 에러 메시지
     */
    @PostMapping
    public ResponseEntity<?> createComment(@PathVariable Long newsfeedId,
                                           @RequestBody CommentRequestDto requestDto,
                                           @RequestHeader("Authorization") String token) {
        try {
            CommentResponseDto response = commentService.addComment(newsfeedId, requestDto, token);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("댓글 내용을 입력하지 않았습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 유효하지 않습니다.");
        }
    }

    /**
     * 지정된 뉴스피드의 모든 댓글을 조회
     * @param newsfeedId 댓글을 조회할 뉴스피드 ID
     * @param token 사용자 인증 토큰
     * @return 댓글 목록 또는 에러 메시지
     */
    @GetMapping
    public ResponseEntity<?> getAllComments(@PathVariable Long newsfeedId,
                                            @RequestHeader("Authorization") String token) {
        try {
            List<CommentResponseDto> responses = commentService.getAllComments(newsfeedId, token);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유효하지 않은 뉴스피드 ID입니다.");
        }
    }

    /**
     * 지정된 댓글을 수정
     * @param newsfeedId 댓글이 위치한 뉴스피드의 ID
     * @param commentId 댓글 ID
     * @param requestDto 수정할 댓글 데이터
     * @param token 사용자 인증 토큰
     * @return 수정된 댓글 정보 또는 에러 메시지
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long newsfeedId,
                                           @PathVariable Long commentId,
                                           @RequestBody CommentRequestDto requestDto,
                                           @RequestHeader("Authorization") String token) {
        try {
            CommentResponseDto response = commentService.updateComment(newsfeedId, commentId, requestDto, token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 댓글 ID입니다.");
        }
    }

    /**
     * 지정된 댓글을 삭제
     * @param newsfeedId 댓글이 위치한 뉴스피드의 ID
     * @param commentId 삭제할 댓글 ID
     * @param token 사용자 인증 토큰
     * @return 삭제 성공 메시지 또는 에러 메시지
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long newsfeedId,
                                           @PathVariable Long commentId,
                                           @RequestHeader("Authorization") String token) {
        try {
            commentService.deleteComment(newsfeedId, commentId, token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 댓글 ID입니다.");
        }
    }
}