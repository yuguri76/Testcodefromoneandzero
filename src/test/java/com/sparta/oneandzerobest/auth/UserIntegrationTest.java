package com.sparta.oneandzerobest.auth;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class UserIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserCreationAndFetch() {
        // given
        User user = User.builder()
                .username("testuser")
                .password("testpassword")
                .name("Test User")
                .email("test@example.com")
                .statusCode(UserStatus.ACTIVE)  // 상태 설정
                .build();

        // when
        userRepository.save(user);
        User fetchedUser = userRepository.findByUsername("testuser").orElse(null);

        // then
        assertNotNull(fetchedUser);
        assertEquals("testuser", fetchedUser.getUsername());
        assertEquals("testpassword", fetchedUser.getPassword());
    }
}
