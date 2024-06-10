package com.sparta.oneandzerobest.profile.service;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.profile.dto.ProfileRequestDto;
import com.sparta.oneandzerobest.profile.dto.ProfileResponseDto;
import com.sparta.oneandzerobest.exception.IncorrectPasswordException;
import com.sparta.oneandzerobest.exception.NotFoundUserException;
import com.sparta.oneandzerobest.exception.PasswordPatternException;
import com.sparta.oneandzerobest.exception.UnacceptablePasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 해당 id를 가진 user의 프로필을 조회해주는 메서드
     *
     * @param id : userId
     * @return : ProfileResponseDto
     */
    public ProfileResponseDto inquiryProfile(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundUserException()
        );

        return new ProfileResponseDto(user);
    }

    /**
     * 프로필을 수정해주는 메서드
     *
     * @param id         : userId
     * @param requestDto : 수정 정보가 담긴 requestDto
     * @return : ProfileResponseDto
     */
    public ProfileResponseDto editProfile(Long id, ProfileRequestDto requestDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundUserException()
        );

        user.update(requestDto); // 이름, 한줄소개 수정

        if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) { // 비밀번호가 입력되어 있을때만 비밀번호 검사 시작
            if (passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) { // DB에 저장된 비밀번호와 일치하지 않는지 검사
                validationPassword(requestDto, user.getPassword()); // 유효한 비밀번호로 변경하려는지 검사
                user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword())); // 비밀번호 수정
            } else {
                throw new IncorrectPasswordException();
            }
        }

        return new ProfileResponseDto(userRepository.save(user));
    }

    /**
     * 유효한 비밀번호로 변경하려는지 검사하는 메서드
     *
     * @param requestDto   : 유저가 입력한 입력한 기존 비밀번호와 새 비밀번호 정보가 담겨있다.
     * @param userPassword : 원래 DB에 저장되어 있던 유저의 비밀번호
     */
    private void validationPassword(ProfileRequestDto requestDto, String userPassword) {
        if (!requestDto.getNewPassword().matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{10,}$")) { // 비밀번호를 올바른 형식으로 바꾸려는 검사
            throw new PasswordPatternException();
        }

        if (passwordEncoder.matches(requestDto.getNewPassword(), userPassword)) { // 이전과 같은 비밀번호로 수정하는지 검사
            throw new UnacceptablePasswordException();
        }
    }
}
