package com.sparta.oneandzerobest.profile.dto;

import lombok.Getter;

@Getter
public class ProfileRequestDto {
    private String name;
    private String email;
    private String introduction;
    private String password;
    private String newPassword;
}
