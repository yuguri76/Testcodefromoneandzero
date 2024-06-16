package com.sparta.oneandzerobest.auth;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserDetails;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.repository.UserDetailsRepository;
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
public class UserDetailsIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Test
    public void testUserDetailsCreationAndFetch() {
        // given
        User user = User.builder()
                .username("testuser")
                .password("testpassword")
                .name("Test User")
                .email("test@example.com")
                .statusCode(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        UserDetails userDetails = UserDetails.builder()
                .user(user)
                .address("Test Address")
                .phoneNumber("1234567890")
                .build();
        userDetailsRepository.save(userDetails);

        // when
        UserDetails fetchedUserDetails = userDetailsRepository.findByUser(user).orElse(null);

        // then
        assertNotNull(fetchedUserDetails);
        assertEquals("Test Address", fetchedUserDetails.getAddress());
        assertEquals("1234567890", fetchedUserDetails.getPhoneNumber());
    }
}
