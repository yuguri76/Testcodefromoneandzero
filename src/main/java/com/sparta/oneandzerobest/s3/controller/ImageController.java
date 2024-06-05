package com.sparta.oneandzerobest.s3.controller;

import com.sparta.oneandzerobest.s3.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService s3UploadService;

    @PostMapping("profile/{id}")
    public String saveFile(@RequestPart(value = "image", required = false) MultipartFile image) {
        String profileImage = s3UploadService.upload(image);
        return "ok";
    }

    @PostMapping("/newfeed/{id}")
    public ResponseEntity<String> uploadImageToNewsfeed(@RequestParam("file") MultipartFile file,
        @RequestParam Long id) {

        return s3UploadService.uploadImageToNewsfeed(id,file);

    }
}
