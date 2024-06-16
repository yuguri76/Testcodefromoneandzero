package com.sparta.oneandzerobest.comment.service;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.dto.CommentResponseDto;
import com.sparta.oneandzerobest.comment.entity.Comment;
import com.sparta.oneandzerobest.comment.repository.CommentRepository;
import com.sparta.oneandzerobest.exception.CommentNotFoundException;
import com.sparta.oneandzerobest.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public CommentResponseDto addComment(Long newsfeedId, CommentRequestDto requestDto, String token) {
        Long userId = validateToken(token);
        Comment comment = new Comment(newsfeedId, userId, requestDto.getContent());
        comment = commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getAllComments(Long newsfeedId, String token) {
        validateToken(token);
        List<Comment> comments = commentRepository.findByNewsfeedId(newsfeedId);
        return comments.stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDto updateComment(Long newsfeedId, Long commentId, CommentRequestDto requestDto, String token) {
        Long userId = validateToken(token);
        Comment comment = commentRepository.findByIdAndNewsfeedIdAndUserId(commentId, newsfeedId, userId)
                .orElseThrow(() -> new CommentNotFoundException("해당 댓글이 존재하지 않거나 권한이 없습니다."));
        comment.setContent(requestDto.getContent());
        comment.setModifiedAt(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    @Transactional
    public void deleteComment(Long newsfeedId, Long commentId, String token) {
        Long userId = validateToken(token);
        Comment comment = commentRepository.findByIdAndNewsfeedIdAndUserId(commentId, newsfeedId, userId)
                .orElseThrow(() -> new CommentNotFoundException("해당 댓글이 존재하지 않거나 권한이 없습니다."));
        commentRepository.delete(comment);
    }

    private Long validateToken(String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("인증 정보가 유효하지 않습니다."));
        return user.getId();
    }
}