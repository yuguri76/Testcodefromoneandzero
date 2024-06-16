package com.sparta.oneandzerobest.profile.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileRequestDto {
    private String name;
    private String introduction;
    private String password;
    private String newPassword;

    @Builder
    public ProfileRequestDto(String name, String introduction, String password, String newPassword) {
        this.name = name;
        this.introduction = introduction;
        this.password = password;
        this.newPassword = newPassword;
    }
}