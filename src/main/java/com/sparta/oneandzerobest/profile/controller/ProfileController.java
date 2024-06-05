package com.sparta.oneandzerobest.profile.controller;

import com.sparta.oneandzerobest.profile.dto.ProfileRequestDto;
import com.sparta.oneandzerobest.profile.dto.ProfileResponseDto;
import com.sparta.oneandzerobest.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

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
}
