package com.sparta.oneandzerobest.newsfeed.controller;

import com.sparta.oneandzerobest.exception.UnauthorizedException;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.service.NewsfeedService;
import com.sparta.oneandzerobest.s3.service.ImageService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/newsfeed")
public class NewsfeedController {

    private final NewsfeedService newsfeedService;
    private final ImageService s3UploadService;

    @Autowired
    public NewsfeedController(NewsfeedService newsfeedService, ImageService s3UploadService) {
        this.newsfeedService = newsfeedService;
        this.s3UploadService = s3UploadService;
    }

    @PostMapping
    public ResponseEntity<NewsfeedResponseDto> postNewsfeed(@RequestHeader("Authorization") String token, @RequestBody NewsfeedRequestDto requestDto) {
        return newsfeedService.postContent(token, requestDto);
    }

    @GetMapping
    public ResponseEntity<Page<NewsfeedResponseDto>> getAllNewsfeed(
            @RequestHeader("Authorization") String token,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam boolean isASC,
            @RequestParam boolean like,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {

        LocalDateTime start = startTime != null ? LocalDateTime.parse(startTime) : null;
        LocalDateTime end = endTime != null ? LocalDateTime.parse(endTime) : null;

        return newsfeedService.getAllContents(page, size, isASC, like, start, end);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteNewsfeed(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return newsfeedService.deleteContent(token, id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsfeedResponseDto> putNewsfeed(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody NewsfeedRequestDto requestDto) {
        return newsfeedService.putContent(token, id, requestDto);
    }

    @PostMapping("/media")
    public ResponseEntity<String> uploadImageToNewsfeed(@RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile file, @RequestParam Long id) {
        return s3UploadService.uploadImageToNewsfeed(token, id, file);
    }

    @PutMapping("/media")
    public ResponseEntity<String> updateImageToNewsfeed(@RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile file, @RequestParam Long id, @RequestParam Long fileid) {
        return s3UploadService.updateImageToNewsfeed(token, file, id, fileid);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedException(UnauthorizedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}