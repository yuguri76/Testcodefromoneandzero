package com.sparta.oneandzerobest.profile.controller;

import com.sparta.oneandzerobest.profile.dto.ProfileRequestDto;
import com.sparta.oneandzerobest.profile.dto.ProfileResponseDto;
import com.sparta.oneandzerobest.profile.service.ProfileService;
import com.sparta.oneandzerobest.s3.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final ImageService s3UploadService;

    /**
     * 선택한 프로필을 조회
     * @param id : userId
     * @return : body로 유저의 정보 전달
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> inquiryProfile(@PathVariable Long id) {
        return ResponseEntity.status(200).body(profileService.inquiryProfile(id));
    }

    /**
     * 프로필 수정
     * @param id : userId
     * @param requestDto : 수정하고자하는 정보를 body로 받아옴
     * @return : body로 유저의 정보 전달
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> editProfile(@PathVariable Long id, @RequestBody ProfileRequestDto requestDto) {
        return ResponseEntity.status(200).body(profileService.editProfile(id, requestDto));
    }

    /**
     * 프로필 사진 올리는 메서드
     * @param image : image file
     * @param id : user id
     * @return : message
     */
    @PostMapping("/{id}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile image, @RequestParam Long id) {
        return s3UploadService.uploadImageToProfile(id,image);
    }
}
