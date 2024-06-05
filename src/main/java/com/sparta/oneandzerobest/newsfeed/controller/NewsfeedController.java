package com.sparta.oneandzerobest.newsfeed.controller;

import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.service.NewsfeedService;
import com.sparta.oneandzerobest.s3.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class NewsfeedController {

    private final NewsfeedService contentService;
    private final ImageService s3UploadService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 뉴스피드 작성
    @PostMapping("/newsfeed")
    public ResponseEntity<NewsfeedResponseDto> postNewsfeed(
        @RequestHeader("Authorization") String token,
        @Valid @RequestBody NewsfeedRequestDto contentRequestDto) {

        return contentService.postContent(token, contentRequestDto);
    }

    // 모든 뉴스피드 조회
    // 인증 x
    @GetMapping("/newsfeed")
    public ResponseEntity<Page<NewsfeedResponseDto>> getAllNewsfeed(
        @RequestParam("page") int page,
        @RequestParam("size") int size) {

        return contentService.getAllContents(page, size);
    }

    // 뉴스피드 수정
    @PutMapping("newsfeed/{id}")
    public ResponseEntity<NewsfeedResponseDto> putNewsfeed(
        @RequestHeader("Authorization") String token, @PathVariable Long id,
        @Valid @RequestBody NewsfeedRequestDto contentRequestDto) {

        return contentService.putContent(token, id, contentRequestDto);
    }

    // 뉴스피드 삭제
    @DeleteMapping("newsfeed/{id}")
    public ResponseEntity<Long> deleteNewsfeed(@RequestHeader("Authorization") String token,
        @PathVariable Long id) {
        return contentService.deleteContent(token, id);
    }

    // 뉴스피드 사진 올리기
    @PostMapping("/newsfeed/{id}")
    public ResponseEntity<String> uploadImageToNewsfeed(@RequestParam("file") MultipartFile file,
                                                        @RequestParam Long id) {

        return s3UploadService.uploadImageToNewsfeed(id,file);
    }
}
