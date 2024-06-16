package com.sparta.oneandzerobest.auth.entity;

import com.sparta.oneandzerobest.profile.dto.ProfileRequestDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserCreation() {
        // given
        String username = "testuser";
        String password = "password";
        String name = "Test User";
        String email = "test@example.com";
        UserStatus status = UserStatus.ACTIVE;

        // when
        User user = new User(username, password, name, email, status);

        // then
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(status, user.getStatusCode());
    }

    @Test
    public void testUserUpdate() {
        // given
        User user = new User("testuser", "password", "Test User", "test@example.com", UserStatus.ACTIVE);
        ProfileRequestDto requestDto = ProfileRequestDto.builder()
                .name("Updated User")
                .introduction("Updated Introduction")
                .build();

        // when
        user.update(requestDto);

        // then
        assertEquals("Updated User", user.getName());
        assertEquals("Updated Introduction", user.getIntroduction());
    }
}