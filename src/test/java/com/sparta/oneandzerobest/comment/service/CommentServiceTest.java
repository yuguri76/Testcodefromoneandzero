package com.sparta.oneandzerobest.comment.service;

import com.sparta.oneandzerobest.auth.config.JwtConfig;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.dto.CommentResponseDto;
import com.sparta.oneandzerobest.comment.entity.Comment;
import com.sparta.oneandzerobest.comment.repository.CommentRepository;
import com.sparta.oneandzerobest.exception.CommentNotFoundException;
import com.sparta.oneandzerobest.exception.UnauthorizedException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    private String validToken;
    private String invalidToken;

    @BeforeEach
    void setUp() {
        byte[] strongKeyBytes = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256).getEncoded();
        String strongKey = Base64.getEncoder().encodeToString(strongKeyBytes);

        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecretKey(strongKey);
        jwtConfig.setTokenExpiration(1000L * 60 * 60); // 1 hour
        jwtConfig.setRefreshTokenExpiration(1000L * 60 * 60 * 24); // 24 hours

        JwtUtil.init(jwtConfig);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(User.builder().id(1L).username("testuser").build()));

        validToken = JwtUtil.createAccessToken("testuser");
        invalidToken = "invalid.token.format";
    }

    @Test
    public void testAddComment_Unauthorized() {
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .newsfeedId(1L)
                .content("This is a test comment")
                .build();

        assertThrows(UnauthorizedException.class, () -> commentService.addComment(1L, requestDto, invalidToken));
    }
    @Test
    public void testAddComment_Success() {
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .newsfeedId(1L)
                .content("This is a test comment")
                .build();

        Comment savedComment = new Comment(1L, 1L, "This is a test comment");
        savedComment.setCreatedAt(LocalDateTime.now());
        savedComment.setModifiedAt(LocalDateTime.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentResponseDto response = commentService.addComment(1L, requestDto, "Bearer " + validToken);
        assert(response != null);
        assert(response.getContent().equals("This is a test comment"));
    }

    @Test
    public void testGetAllComments_Success() {
        Comment comment1 = new Comment(1L, 1L, "Comment 1");
        comment1.setCreatedAt(LocalDateTime.now());
        comment1.setModifiedAt(LocalDateTime.now());

        Comment comment2 = new Comment(1L, 1L, "Comment 2");
        comment2.setCreatedAt(LocalDateTime.now());
        comment2.setModifiedAt(LocalDateTime.now());

        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(commentRepository.findByNewsfeedId(anyLong())).thenReturn(comments);

        List<CommentResponseDto> response = commentService.getAllComments(1L, "Bearer " + validToken);
        assert(response.size() == 2);
        assert(response.get(0).getContent().equals("Comment 1"));
        assert(response.get(1).getContent().equals("Comment 2"));
    }

    @Test
    public void testUpdateComment_NotFound() {
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .newsfeedId(1L)
                .content("Updated content")
                .build();

        when(commentRepository.findByIdAndNewsfeedIdAndUserId(anyLong(), anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(1L, 1L, requestDto, "Bearer " + validToken));
    }

    @Test
    public void testDeleteComment_NotFound() {
        when(commentRepository.findByIdAndNewsfeedIdAndUserId(anyLong(), anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(1L, 1L, "Bearer " + validToken));
    }
}
