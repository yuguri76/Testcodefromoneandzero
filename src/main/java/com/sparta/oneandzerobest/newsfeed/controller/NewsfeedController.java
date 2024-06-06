package com.sparta.oneandzerobest.newsfeed.controller;

import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.service.NewsfeedService;
import com.sparta.oneandzerobest.s3.service.ImageService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
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

    private final NewsfeedService newsfeedService;
    private final ImageService s3UploadService;

    /**
     * 뉴스피드 생성
     * @param token
     * @param contentRequestDto
     * @return
     */
    @PostMapping("/newsfeed")
    public ResponseEntity<NewsfeedResponseDto> postNewsfeed(
        @RequestHeader("Authorization") String token,
        @Valid @RequestBody NewsfeedRequestDto contentRequestDto) {

        return newsfeedService.postContent(token, contentRequestDto);
    }

    /**
     * 뉴스피드 조회 (페이지)
     * @param page
     * @param size
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/newsfeed")
    public ResponseEntity<Page<NewsfeedResponseDto>> getAllNewsfeed(
        @RequestParam("page") int page,
        @RequestParam("size") int size,
        @RequestParam(required = false) LocalDateTime startTime,
        @RequestParam(required = false) LocalDateTime endTime) {

        return newsfeedService.getAllContents(page, size, startTime, endTime);
    }

    /**
     * 뉴스피드 수정
     * @param token
     * @param id
     * @param contentRequestDto
     * @return
     */
    @PutMapping("newsfeed/{id}")
    public ResponseEntity<NewsfeedResponseDto> putNewsfeed(
        @RequestHeader("Authorization") String token, @PathVariable Long id,
        @Valid @RequestBody NewsfeedRequestDto contentRequestDto) {

        return newsfeedService.putContent(token, id, contentRequestDto);
    }

    /**
     * 뉴스피드 삭제
     * @param token
     * @param id
     * @return
     */
    @DeleteMapping("newsfeed/{id}")
    public ResponseEntity<Long> deleteNewsfeed(@RequestHeader("Authorization") String token,
        @PathVariable Long id) {
        return newsfeedService.deleteContent(token, id);
    }

    /**
     * 뉴스피드에 사진 업로드
     * @param file
     * @param id
     * @return
     */
    @PostMapping("/newsfeed/media")
    public ResponseEntity<String> uploadImageToNewsfeed(@RequestParam("file") MultipartFile file,
                                                        @RequestParam Long id) {

        return s3UploadService.uploadImageToNewsfeed(id,file);
    }

    @PutMapping("/newsfeed/media")
    public ResponseEntity<String> updateImageToNewsfeed(@RequestParam("file") MultipartFile file,
        @RequestParam Long id, @RequestParam Long fileid) {

        return s3UploadService.updateImageToNewsfeed(file,id,fileid);
    }
}
