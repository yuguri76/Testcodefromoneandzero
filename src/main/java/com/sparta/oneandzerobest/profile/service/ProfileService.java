package com.sparta.oneandzerobest.profile.service;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.profile.dto.ProfileRequestDto;
import com.sparta.oneandzerobest.profile.dto.ProfileResponseDto;
import com.sparta.oneandzerobest.profile.exception.IncorrectPasswordException;
import com.sparta.oneandzerobest.profile.exception.NotFoundUserException;
import com.sparta.oneandzerobest.profile.exception.PasswordPatternException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileResponseDto inquiryProfile(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundUserException()
        );

        ProfileResponseDto responseDto = new ProfileResponseDto(user);
        return responseDto;
    }

    public ProfileResponseDto editProfile(Long id, ProfileRequestDto requestDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundUserException()
        );

        if (requestDto.getPassword() != null) {
            if (passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
                if (!requestDto.getNewPassword().matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{10,}$")) {
                    throw new PasswordPatternException();
                }
                user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
            } else {
                throw new IncorrectPasswordException();
            }
        } else {
            user.update(requestDto);
        }

        ProfileResponseDto responseDto = new ProfileResponseDto(userRepository.save(user));
        return responseDto;
    }
}
