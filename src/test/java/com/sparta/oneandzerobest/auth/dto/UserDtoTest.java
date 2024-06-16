package com.sparta.oneandzerobest.auth.dto;

import com.sparta.oneandzerobest.auth.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDtoTest {

    @Test
    public void testUserDto() {
        // given
        User user = User.builder()
                .username("testuser")
                .password("testpassword")
                .build();

        // when
        UserDto userDto = new UserDto(user);

        // then
        assertEquals("testuser", userDto.getUsername());
        assertEquals("testpassword", userDto.getPassword());
    }
}