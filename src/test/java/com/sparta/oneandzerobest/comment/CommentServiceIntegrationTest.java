package com.sparta.oneandzerobest.comment;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.entity.Comment;
import com.sparta.oneandzerobest.comment.repository.CommentRepository;
import com.sparta.oneandzerobest.comment.service.CommentService;
import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import com.sparta.oneandzerobest.newsfeed.repository.NewsfeedRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NewsfeedRepository newsfeedRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAddComment() {
        // given
        User user = User.builder()
                .username("testuser")
                .password("testpassword")
                .name("Test User")
                .email("test@example.com")
                .statusCode(UserStatus.ACTIVE)  // 상태 설정
                .build();
        userRepository.save(user);

        Newsfeed newsfeed = Newsfeed.builder()
                .userid(user.getId())
                .content("This is a test newsfeed content")
                .build();
        newsfeedRepository.save(newsfeed);

        CommentRequestDto requestDto = CommentRequestDto.builder()
                .newsfeedId(newsfeed.getId())
                .content("꿈을 꾸면 무엇이든지 될 수 있어~")
                .build();

        // when
        commentService.addComment(newsfeed.getId(), requestDto, "Bearer " + JwtUtil.createAccessToken(user.getUsername()));
        Comment fetchedComment = commentRepository.findAll().get(0);

        // then
        assertNotNull(fetchedComment);
        assertEquals("꿈을 꾸면 무엇이든지 될 수 있어~", fetchedComment.getContent());
    }
}
