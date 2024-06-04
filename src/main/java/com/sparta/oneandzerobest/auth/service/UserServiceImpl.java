package com.sparta.oneandzerobest.auth.service;


import com.sparta.oneandzerobest.auth.entity.LoginRequest;
import com.sparta.oneandzerobest.auth.entity.SignupRequest;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserRoleEnum;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.exception.InfoNotCorrectedException;
import com.sparta.oneandzerobest.exception.InvalidPasswordException;
import com.sparta.oneandzerobest.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "e36f112d-c6f2-466f-aad8-14dcdc16360b";

    // 회원가입
    public void signup(SignupRequest signupRequest) {
        String username = signupRequest.getUsername();

        // username 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)
        if (!username.matches("^[a-z0-9]{4,10}$")) {
            throw new IllegalArgumentException("아이디는 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z)와 숫자(0~9)로 구성되어야 합니다.");

        }

        String encodedPassword = passwordEncoder.encode( signupRequest.getPassword());

        // password  최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9)
        if (!signupRequest.getPassword().matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,15}$")) {
            throw new IllegalArgumentException("비밀번호은 최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z)와 숫자(0~9)로 구성되어야 합니다.");
        }


        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new InfoNotCorrectedException("중복된 사용자가 존재합니다.");
        }

        // email 중복확인
        String email = signupRequest.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new InfoNotCorrectedException("중복된 Email 입니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (signupRequest.isAdmin()) {
            if (!ADMIN_TOKEN.equals(signupRequest.getAdminToken())) {
                throw new UnauthorizedException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        // 새로운 사용자 객체 생성 - 등록
        User user = new User(username, encodedPassword, email, role);
        userRepository.save(user);
        log.warn("회원가입 성공: " + user.getUsername());
    }

    // 로그인
    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new InfoNotCorrectedException("이름과 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호와 비밀번호가 비교하지 않음.");
        }

        String token = jwtUtil.createAccessToken(user.getUsername());
        log.info("로그인 성공: 사용자 {}, 토큰 {}", user.getUsername(), token);
        return token;
    }
}

