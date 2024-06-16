package com.sparta.oneandzerobest.newsfeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.exception.UnauthorizedException;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.service.NewsfeedService;
import com.sparta.oneandzerobest.s3.service.ImageService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NewsfeedControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NewsfeedService newsfeedService;

    @Mock
    private ImageService s3UploadService;

    @InjectMocks
    private NewsfeedController newsfeedController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(newsfeedController).build();
    }

    @Test
    public void testPostNewsfeed() throws Exception {
        // Given
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("Test content");
        NewsfeedResponseDto responseDto = NewsfeedResponseDto.builder()
                .id(1L)
                .userId(1L)
                .content("Test content")
                .createdAt(LocalDateTime.now())
                .build();

        Claims claims = Jwts.claims();
        claims.setSubject("testUser");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(anyString())).thenReturn(claims);

            doReturn(new ResponseEntity<>(responseDto, HttpStatus.OK)).when(newsfeedService).postContent(anyString(), any(NewsfeedRequestDto.class));

            // When and Then
            mockMvc.perform(post("/newsfeed")
                            .header("Authorization", "Bearer testToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.userId").value(1L))
                    .andExpect(jsonPath("$.content").value("Test content"));
        }
    }

    @Test
    public void testPostNewsfeed_Unauthorized() throws Exception {
        // Given
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("Test content");

        Claims claims = Jwts.claims();
        claims.setSubject("testUser");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(anyString())).thenReturn(claims);

            doThrow(new UnauthorizedException("Invalid token")).when(newsfeedService).postContent(anyString(), any(NewsfeedRequestDto.class));

            // When and Then
            mockMvc.perform(post("/newsfeed")
                            .header("Authorization", "Bearer testToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid token"));
        }
    }


    @Test
    public void testGetAllNewsfeed() throws Exception {
        // Given
        Claims claims = Jwts.claims();
        claims.setSubject("testUser");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(anyString())).thenReturn(claims);

            List<NewsfeedResponseDto> responseList = Collections.emptyList();
            Pageable pageable = PageRequest.of(0, 10);
            Page<NewsfeedResponseDto> responsePage = new PageImpl<>(responseList, pageable, responseList.size());
            doReturn(new ResponseEntity<>(responsePage, HttpStatus.OK)).when(newsfeedService).getAllContents(anyInt(), anyInt(), anyBoolean(), anyBoolean(), any(), any());

            // When and Then
            mockMvc.perform(get("/newsfeed")
                            .param("page", "0")
                            .param("size", "10")
                            .param("isASC", "true")
                            .param("like", "false")
                            .param("startTime", "2023-01-01T00:00:00")
                            .param("endTime", "2023-12-31T23:59:59")
                            .header("Authorization", "Bearer testToken")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }


    @Test
    public void testGetAllNewsfeed_Unauthorized() throws Exception {
        // Given
        Claims claims = Jwts.claims();
        claims.setSubject("testUser");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(anyString())).thenReturn(claims);

            doThrow(new UnauthorizedException("Invalid token")).when(newsfeedService).getAllContents(anyInt(), anyInt(), anyBoolean(), anyBoolean(), any(), any());

            // When and Then
            mockMvc.perform(get("/newsfeed")
                            .param("page", "0")
                            .param("size", "10")
                            .param("isASC", "true")
                            .param("like", "false")
                            .param("startTime", "2023-01-01T00:00:00")
                            .param("endTime", "2023-12-31T23:59:59")
                            .header("Authorization", "Bearer testToken")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid token"));
        }
    }

    @Test
    public void testGetAllNewsfeed_WithTimeRange() throws Exception {
        // Given
        Claims claims = Jwts.claims();
        claims.setSubject("testUser");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(anyString())).thenReturn(claims);

            List<NewsfeedResponseDto> responseList = Collections.emptyList();
            Pageable pageable = PageRequest.of(0, 10);
            Page<NewsfeedResponseDto> responsePage = new PageImpl<>(responseList, pageable, responseList.size());
            doReturn(new ResponseEntity<>(responsePage, HttpStatus.OK)).when(newsfeedService).getAllContents(anyInt(), anyInt(), anyBoolean(), anyBoolean(), any(), any());

            // When and Then
            mockMvc.perform(get("/newsfeed")
                            .param("page", "0")
                            .param("size", "10")
                            .param("isASC", "true")
                            .param("like", "false")
                            .param("startTime", "2023-01-01T00:00:00")
                            .param("endTime", "2023-12-31T23:59:59")
                            .header("Authorization", "Bearer testToken")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }


    @Test
    public void testDeleteNewsfeed() throws Exception {
        // Given
        Claims claims = Jwts.claims();
        claims.setSubject("testUser");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(anyString())).thenReturn(claims);

            // Setup newsfeedService.deleteContent to return ResponseEntity
            doReturn(new ResponseEntity<>(1L, HttpStatus.OK)).when(newsfeedService).deleteContent(anyString(), anyLong());

            // When and Then
            mockMvc.perform(delete("/newsfeed/1")
                            .header("Authorization", "Bearer testToken")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void testDeleteNewsfeed_Unauthorized() throws Exception {
        // Given
        Claims claims = Jwts.claims();
        claims.setSubject("testUser");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(anyString())).thenReturn(claims);

            doThrow(new UnauthorizedException("Invalid token")).when(newsfeedService).deleteContent(anyString(), anyLong());

            // When and Then
            mockMvc.perform(delete("/newsfeed/1")
                            .header("Authorization", "Bearer testToken")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid token"));
        }
    }

    @Test
    public void testPutNewsfeed() throws Exception {
        // Given
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("Updated content");
        NewsfeedResponseDto responseDto = NewsfeedResponseDto.builder()
                .id(1L)
                .userId(1L)
                .content("Updated content")
                .createdAt(LocalDateTime.now())
                .build();

        Claims claims = Jwts.claims();
        claims.setSubject("testUser");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(anyString())).thenReturn(claims);

            doReturn(new ResponseEntity<>(responseDto, HttpStatus.OK)).when(newsfeedService).putContent(anyString(), anyLong(), any(NewsfeedRequestDto.class));

            // When and Then
            mockMvc.perform(put("/newsfeed/1")
                            .header("Authorization", "Bearer testToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.userId").value(1L))
                    .andExpect(jsonPath("$.content").value("Updated content"));
        }
    }

    @Test
    public void testPutNewsfeed_Unauthorized() throws Exception {
        // Given
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("Updated content");

        Claims claims = Jwts.claims();
        claims.setSubject("testUser");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(anyString())).thenReturn(claims);

            doThrow(new UnauthorizedException("Invalid token")).when(newsfeedService).putContent(anyString(), anyLong(), any(NewsfeedRequestDto.class));

            // When and Then
            mockMvc.perform(put("/newsfeed/1")
                            .header("Authorization", "Bearer testToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid token"));
        }
    }

    @Test
    public void testUpdateImageToNewsfeed() throws Exception {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "Test Image Content".getBytes());

        Claims claims = Jwts.claims();
        claims.setSubject("testUser");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(anyString())).thenReturn(claims);

            doReturn(new ResponseEntity<>("Image updated", HttpStatus.OK)).when(s3UploadService).updateImageToNewsfeed(anyString(), any(), anyLong(), anyLong());

            // When and Then
            mockMvc.perform(multipart("/newsfeed/media")
                            .file(imageFile)
                            .param("id", "1")
                            .param("fileid", "1")
                            .header("Authorization", "Bearer testToken"))
                    .andExpect(status().isOk());
        }
    }


    @Test
    public void testUploadImageToNewsfeed() throws Exception {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "Test Image Content".getBytes());

        Claims claims = Jwts.claims();
        claims.setSubject("testUser");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(anyString())).thenReturn(claims);

            doReturn(new ResponseEntity<>("Image uploaded", HttpStatus.OK)).when(s3UploadService).uploadImageToNewsfeed(anyString(), anyLong(), any());

            // When and Then
            mockMvc.perform(multipart("/newsfeed/media")
                            .file(imageFile)
                            .param("id", "1")
                            .header("Authorization", "Bearer testToken"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void testUploadImageToNewsfeed_Unauthorized() throws Exception {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "Test Image Content".getBytes());

        Claims claims = Jwts.claims();
        claims.setSubject("testUser");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(anyString())).thenReturn(claims);

            doThrow(new UnauthorizedException("Invalid token")).when(s3UploadService).uploadImageToNewsfeed(anyString(), anyLong(), any());

            // When and Then
            mockMvc.perform(multipart("/newsfeed/media")
                            .file(imageFile)
                            .param("id", "1")
                            .header("Authorization", "Bearer testToken"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid token"));
        }
    }
}
