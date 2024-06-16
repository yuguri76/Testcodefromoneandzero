package com.sparta.oneandzerobest.aop;

import com.sparta.oneandzerobest.comment.controller.CommentController;
import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class LoggingAspectTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .build();
    }

    @Test
    public void testLogAspect() throws Exception {
        // given
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .newsfeedId(1L)
                .content("Test comment")
                .build();

        when(commentService.addComment(1L, requestDto, "Bearer token")).thenReturn(null);

        // when & then
        mockMvc.perform(post("/newsfeed/1/comment")
                        .header("Authorization", "Bearer token")
                        .contentType("application/json")
                        .content("{\"newsfeedId\": 1, \"content\": \"Test comment\"}"))
                .andExpect(status().isCreated());
    }
}
