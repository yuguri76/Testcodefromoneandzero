package com.sparta.oneandzerobest.comment.controller;

import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.dto.CommentResponseDto;
import com.sparta.oneandzerobest.comment.service.CommentService;
import com.sparta.oneandzerobest.exception.CommentNotFoundException;
import com.sparta.oneandzerobest.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/newsfeed/{newsfeedId}/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(@PathVariable Long newsfeedId,
                                           @RequestBody CommentRequestDto requestDto,
                                           @RequestHeader("Authorization") String token) {
        try {
            CommentResponseDto response = commentService.addComment(newsfeedId, requestDto, token);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("댓글 내용을 입력하지 않았습니다.");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllComments(@PathVariable Long newsfeedId,
                                            @RequestHeader("Authorization") String token) {
        List<CommentResponseDto> responses = commentService.getAllComments(newsfeedId, token);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long newsfeedId,
                                           @PathVariable Long commentId,
                                           @RequestBody CommentRequestDto requestDto,
                                           @RequestHeader("Authorization") String token) {
        CommentResponseDto response = commentService.updateComment(newsfeedId, commentId, requestDto, token);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long newsfeedId,
                                           @PathVariable Long commentId,
                                           @RequestHeader("Authorization") String token) {
        commentService.deleteComment(newsfeedId, commentId, token);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 유효하지 않습니다.");
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 댓글이 존재하지 않거나 권한이 없습니다.");
    }
}