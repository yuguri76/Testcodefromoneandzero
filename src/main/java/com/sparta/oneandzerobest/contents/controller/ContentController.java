package com.sparta.oneandzerobest.contents.controller;


import com.sparta.oneandzerobest.contents.dto.ContentRequestDto;
import com.sparta.oneandzerobest.contents.dto.ContentResponseDto;
import com.sparta.oneandzerobest.contents.service.ContentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PostMapping("/contents")
    public ResponseEntity<ContentResponseDto> postContent( @RequestBody ContentRequestDto contentRequestDto) {

        return contentService.postContent(contentRequestDto);
    }

    @GetMapping("/contents")
    public ResponseEntity<List<ContentResponseDto>> getAllContents() {

        return contentService.getAllContents();
    }

    @PutMapping("contents/{id}")
    public ResponseEntity<ContentResponseDto> putContent(@RequestBody ContentRequestDto contentRequestDto) {

        return contentService.putContent(contentRequestDto);
    }
}
