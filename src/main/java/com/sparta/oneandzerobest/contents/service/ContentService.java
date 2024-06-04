package com.sparta.oneandzerobest.contents.service;

import com.sparta.oneandzerobest.contents.dto.ContentRequestDto;
import com.sparta.oneandzerobest.contents.dto.ContentResponseDto;
import com.sparta.oneandzerobest.contents.repository.ContentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public ResponseEntity<ContentResponseDto> postContent(ContentRequestDto contentRequestDto) {

        return null;

    }

    public ResponseEntity<List<ContentResponseDto>> getAllContents() {
        return null;
    }

    public ResponseEntity<ContentResponseDto> putContent(ContentRequestDto contentRequestDto) {
        return null;
    }
}
