package com.sparta.oneandzerobest.newsfeed.service;

import com.sparta.oneandzerobest.auth.config.JwtConfig;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.exception.UnauthorizedException;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import com.sparta.oneandzerobest.newsfeed.repository.NewsfeedRepository;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // lenient 모드 설정
public class NewsfeedServiceTest {

    @InjectMocks
    private NewsfeedService newsfeedService;

    @Mock
    private NewsfeedRepository newsfeedRepository;

    @Mock
    private UserRepository userRepository;

    private String validToken;
    private String invalidToken;

    @BeforeEach
    void setUp() {
        // Generate a strong key using io.jsonwebtoken.security.Keys
        byte[] strongKeyBytes = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256).getEncoded();
        String strongKey = Base64.getEncoder().encodeToString(strongKeyBytes);

        // Initialize JwtConfig and set values
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecretKey(strongKey);
        jwtConfig.setTokenExpiration(1000L * 60 * 60); // 1 hour
        jwtConfig.setRefreshTokenExpiration(1000L * 60 * 60 * 24); // 24 hours

        // Initialize JwtUtil with JwtConfig
        JwtUtil.init(jwtConfig);

        // Mock the UserRepository to return a user for the username "testuser"
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(User.builder().id(1L).username("testuser").build()));

        // Create a valid token using JwtUtil
        validToken = JwtUtil.createAccessToken("testuser");

        // Create an invalid token (malformed)
        invalidToken = "invalid.token.format";
    }

    @Test
    public void testPostContent_Unauthorized() {
        // given
        NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder()
                .content("This is a test newsfeed content")
                .build();

        // when & then
        assertThrows(UnauthorizedException.class, () -> newsfeedService.postContent(invalidToken, requestDto));
    }

    @Test
    public void testPutContent_NotFound() {
        // given
        NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder()
                .content("Updated content")
                .build();

        when(newsfeedRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> newsfeedService.putContent("Bearer " + validToken, 1L, requestDto));
    }

    @Test
    public void testDeleteContent_NotFound() {
        // given
        when(newsfeedRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> newsfeedService.deleteContent("Bearer " + validToken, 1L));
    }

    @Test
    public void testGetAllContents() {
        // given
        Newsfeed newsfeed1 = Newsfeed.builder()
                .id(1L)
                .userid(1L)
                .content("Content 1")
                .build();

        Newsfeed newsfeed2 = Newsfeed.builder()
                .id(2L)
                .userid(1L)
                .content("Content 2")
                .build();

        Page<Newsfeed> newsfeedPage = new PageImpl<>(Arrays.asList(newsfeed1, newsfeed2));

        when(newsfeedRepository.findAll(any(Pageable.class))).thenReturn(newsfeedPage);

        // when
        ResponseEntity<Page<NewsfeedResponseDto>> response = newsfeedService.getAllContents(0, 10, true, false, null, null);

        // then
        assert(response.getBody() != null);
        assert(response.getBody().getContent().size() == 2);
        assert(response.getBody().getContent().get(0).getContent().equals("Content 1"));
        assert(response.getBody().getContent().get(1).getContent().equals("Content 2"));
    }
}
