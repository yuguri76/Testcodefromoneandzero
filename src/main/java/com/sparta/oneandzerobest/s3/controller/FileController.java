package com.sparta.oneandzerobest.s3.controller;

import com.sparta.oneandzerobest.s3.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final S3UploadService s3UploadService;

    @PostMapping()
    public String saveFile(@RequestPart(value = "image", required = false) MultipartFile image) {
        String profileImage = s3UploadService.upload(image);
        return "ok";
    }
}
