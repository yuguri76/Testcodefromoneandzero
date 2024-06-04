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

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> inquiryProfile(@PathVariable Long id) {
        return ResponseEntity.status(200).body(profileService.inquiryProfile(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> editProfile(@PathVariable Long id, @RequestBody ProfileRequestDto requestDto) {
        return ResponseEntity.status(200).body(profileService.editProfile(id, requestDto));
    }
}
