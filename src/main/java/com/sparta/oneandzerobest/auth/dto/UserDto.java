package com.sparta.oneandzerobest.auth.dto;

import com.sparta.oneandzerobest.auth.entity.User;
import lombok.Getter;

@Getter
public class UserDto {
    private String username;
    private String password;

    public UserDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
    }
}