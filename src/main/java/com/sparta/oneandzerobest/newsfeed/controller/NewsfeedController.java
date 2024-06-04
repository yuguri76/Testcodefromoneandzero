package com.sparta.oneandzerobest.newsfeed.controller;


import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.service.NewsfeedService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NewsfeedController {

    private final NewsfeedService contentService;

    @PostMapping("/contents")
    public ResponseEntity<NewsfeedResponseDto> postContent(@Valid @RequestBody NewsfeedRequestDto contentRequestDto) {

        return contentService.postContent(contentRequestDto);
    }

    @GetMapping("/contents")
    public ResponseEntity<Page<NewsfeedResponseDto>> getAllContents(
        @RequestParam("page")int page,
        @RequestParam("size") int size) {

        return contentService.getAllContents(page,size);
    }

    @PutMapping("contents/{id}")
    public ResponseEntity<NewsfeedResponseDto> putContent(@PathVariable Long id,@Valid @RequestBody NewsfeedRequestDto contentRequestDto) {

        return contentService.putContent(id,contentRequestDto);
    }

    @DeleteMapping("contents/{id}")
    public ResponseEntity<Long> deleteContent(@PathVariable Long id) {
        return contentService.deleteContent(id);
    }
}
