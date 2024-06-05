package com.sparta.oneandzerobest.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

/**
 * UserDetailsService 구현제 서비스
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }
        User user = userOptional.get();
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .build();
    }
    public User saveOrUpdateKakaoUser(String userInfoJson) {
        try {
            JsonNode userInfo = objectMapper.readTree(userInfoJson);

            long kakaoId = userInfo.path("id").asLong();
            String nickname = userInfo.path("properties").path("nickname").asText();
            String profileImage = userInfo.path("properties").path("profile_image").asText();
            String email = kakaoId + "aA@naver.com";

            User user = userRepository.findById(kakaoId).orElse(new User());
            user.setId(kakaoId);
            user.setUsername(nickname);
            user.setEmail(email);
            user.setName(nickname);
            user.setStatusCode("정상");

            return userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse user info", e);
        }
    }
}
