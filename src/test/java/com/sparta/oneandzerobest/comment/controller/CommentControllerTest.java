package com.sparta.oneandzerobest.comment.controller;

import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.dto.CommentResponseDto;
import com.sparta.oneandzerobest.comment.service.CommentService;
import com.sparta.oneandzerobest.exception.CommentNotFoundException;
import com.sparta.oneandzerobest.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    private String validToken;
    private String invalidToken;

    @BeforeEach
    void setUp() {
        validToken = "Bearer valid.token.here";
        invalidToken = "Bearer invalid.token.here";
    }

    @Test
    void testCreateComment_Success() {
        Long newsfeedId = 1L;
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .newsfeedId(newsfeedId)
                .content("This is a test comment")
                .build();
        CommentResponseDto responseDto = CommentResponseDto.builder()
                .id(1L)
                .newsfeedId(newsfeedId)
                .userId(1L)
                .content("This is a test comment")
                .build();

        when(commentService.addComment(anyLong(), any(CommentRequestDto.class), any(String.class)))
                .thenReturn(responseDto);

        ResponseEntity<?> responseEntity = commentController.createComment(newsfeedId, requestDto, validToken);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(responseDto, responseEntity.getBody());
    }

    @Test
    void testGetAllComments_Success() {
        Long newsfeedId = 1L;
        CommentResponseDto comment1 = CommentResponseDto.builder()
                .id(1L)
                .newsfeedId(newsfeedId)
                .userId(1L)
                .content("First comment")
                .build();
        CommentResponseDto comment2 = CommentResponseDto.builder()
                .id(2L)
                .newsfeedId(newsfeedId)
                .userId(2L)
                .content("Second comment")
                .build();
        List<CommentResponseDto> commentList = Arrays.asList(comment1, comment2);

        when(commentService.getAllComments(anyLong(), any(String.class)))
                .thenReturn(commentList);

        ResponseEntity<?> responseEntity = commentController.getAllComments(newsfeedId, validToken);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(commentList, responseEntity.getBody());
    }

    @Test
    void testUpdateComment_Success() {
        Long newsfeedId = 1L;
        Long commentId = 1L;
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .newsfeedId(newsfeedId)
                .content("Updated comment content")
                .build();
        CommentResponseDto responseDto = CommentResponseDto.builder()
                .id(commentId)
                .newsfeedId(newsfeedId)
                .userId(1L)
                .content("Updated comment content")
                .build();

        when(commentService.updateComment(anyLong(), anyLong(), any(CommentRequestDto.class), any(String.class)))
                .thenReturn(responseDto);

        ResponseEntity<?> responseEntity = commentController.updateComment(newsfeedId, commentId, requestDto, validToken);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDto, responseEntity.getBody());
    }

    @Test
    void testDeleteComment_Success() {
        Long newsfeedId = 1L;
        Long commentId = 1L;

        ResponseEntity<?> responseEntity = commentController.deleteComment(newsfeedId, commentId, validToken);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testCreateComment_Unauthorized() {
        Long newsfeedId = 1L;
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .newsfeedId(newsfeedId)
                .content("This is a test comment")
                .build();

        doThrow(new UnauthorizedException("Invalid token"))
                .when(commentService).addComment(anyLong(), any(CommentRequestDto.class), any(String.class));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            commentController.createComment(newsfeedId, requestDto, invalidToken);
        });

        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    void testGetAllComments_Unauthorized() {
        Long newsfeedId = 1L;

        doThrow(new UnauthorizedException("Invalid token"))
                .when(commentService).getAllComments(anyLong(), any(String.class));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            commentController.getAllComments(newsfeedId, invalidToken);
        });

        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    void testUpdateComment_NotFound() {
        Long newsfeedId = 1L;
        Long commentId = 1L;
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .newsfeedId(newsfeedId)
                .content("Updated comment content")
                .build();

        doThrow(new CommentNotFoundException("Comment not found"))
                .when(commentService).updateComment(anyLong(), anyLong(), any(CommentRequestDto.class), any(String.class));

        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class, () -> {
            commentController.updateComment(newsfeedId, commentId, requestDto, validToken);
        });

        assertEquals("Comment not found", exception.getMessage());
    }

    @Test
    void testDeleteComment_NotFound() {
        Long newsfeedId = 1L;
        Long commentId = 1L;

        doThrow(new CommentNotFoundException("Comment not found"))
                .when(commentService).deleteComment(anyLong(), anyLong(), any(String.class));

        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class, () -> {
            commentController.deleteComment(newsfeedId, commentId, validToken);
        });

        assertEquals("Comment not found", exception.getMessage());
    }
}
