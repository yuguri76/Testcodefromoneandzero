package com.sparta.oneandzerobest.auth.service;

import com.sparta.oneandzerobest.auth.dto.TokenResponseDto;
import com.sparta.oneandzerobest.auth.entity.*;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.exception.InfoNotCorrectedException;
import com.sparta.oneandzerobest.exception.InvalidPasswordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 회원가입: ID, PW, Email
     * @param signupRequest
     */
    @Override
    public void signup(SignupRequest signupRequest) {
        String authId = signupRequest.getUsername();
        String password = signupRequest.getPassword();
        String email = signupRequest.getEmail();

        if (!authId.matches("^[a-zA-Z0-9]{10,20}$")) {
            throw new IllegalArgumentException("아이디는 최소 10자 이상, 20자 이하이며 알파벳 대소문자와 숫자로 구성되어야 합니다.");
        }

        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{10,}$")) {
            throw new IllegalArgumentException("비밀번호는 최소 10자 이상이며 알파벳 대소문자, 숫자, 특수문자를 포함해야 합니다.");
        }

        if (userRepository.findByUsername(authId).isPresent()) {
            throw new InfoNotCorrectedException("중복된 사용자 ID가 존재합니다.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new InfoNotCorrectedException("중복된 이메일이 존재합니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(authId, encodedPassword, signupRequest.getUsername(), email, "정상");
        userRepository.save(user);
    }

    /**
     * 로그인: ACCESS TOKEN, REFRESH TOKEN 생성
     * 비밀번호: 암호화
     * 탈퇴한 사람 분별
     * @param loginRequest
     * @return
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new InfoNotCorrectedException("사용자 ID와 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("사용자 ID와 비밀번호가 일치하지 않습니다.");
        }

        if ("탈퇴".equals(user.getStatusCode())) {
            throw new InfoNotCorrectedException("탈퇴한 사용자입니다.");
        }

        String accessToken = jwtUtil.createAccessToken(user.getUsername());
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 로그아웃: 리프레시 토큰 삭제
     * @param username
     */
    @Override
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InfoNotCorrectedException("사용자를 찾을 수 없습니다."));

        user.setRefreshToken(null);
        userRepository.save(user);
    }

    /**
     * 회원 탈퇴 -> 리프레시 토큰 삭제, getStatusCode: 탈퇴
     * @param id: 아이디
     * @param password: 비밀번호
     */
    @Override
    public void withdraw(String id, String password) {
        User user = userRepository.findByUsername(id)
                .orElseThrow(() -> new InfoNotCorrectedException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        if ("탈퇴".equals(user.getStatusCode())) {
            throw new InfoNotCorrectedException("이미 탈퇴한 사용자입니다.");
        }

        user.setStatusCode("탈퇴");
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    @Override
    public TokenResponseDto refresh(String refreshToken) {
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InfoNotCorrectedException("사용자를 찾을 수 없습니다."));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new InvalidPasswordException("리프레시 토큰이 유효하지 않습니다.");
        }

        String newAccessToken = jwtUtil.createAccessToken(username);
        String newRefreshToken = jwtUtil.createRefreshToken(username);

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }
}
