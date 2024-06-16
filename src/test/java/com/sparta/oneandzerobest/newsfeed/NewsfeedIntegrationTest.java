package com.sparta.oneandzerobest.newsfeed;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import com.sparta.oneandzerobest.newsfeed.repository.NewsfeedRepository;
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
public class NewsfeedIntegrationTest {

    @Autowired
    private NewsfeedRepository newsfeedRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testNewsfeedCreationAndFetch() {
        // given
        User user = User.builder()
                .username("testuser")
                .password("testpassword")
                .name("Test User")
                .email("test@example.com")
                .statusCode(UserStatus.ACTIVE)  // 상태 설정
                .build();
        userRepository.save(user);

        Newsfeed newsfeed = Newsfeed.builder()
                .userid(user.getId())
                .content("This is a test newsfeed content")
                .build();
        newsfeedRepository.save(newsfeed);

        // when
        Newsfeed fetchedNewsfeed = newsfeedRepository.findById(newsfeed.getId()).orElse(null);

        // then
        assertNotNull(fetchedNewsfeed);
        assertEquals("This is a test newsfeed content", fetchedNewsfeed.getContent());
    }
}
