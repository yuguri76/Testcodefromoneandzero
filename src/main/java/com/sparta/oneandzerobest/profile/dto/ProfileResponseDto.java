package com.sparta.oneandzerobest.profile.dto;

import com.sparta.oneandzerobest.auth.entity.User;
import lombok.Getter;

@Getter
public class ProfileResponseDto {
    private String username;
    private String name;
    private String introduction;
    private String email;

    public ProfileResponseDto(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.introduction = user.getIntroduction();
        this.email = user.getEmail();
    }
}
