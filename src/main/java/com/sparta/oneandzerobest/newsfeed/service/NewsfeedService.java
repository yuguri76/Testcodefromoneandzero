package com.sparta.oneandzerobest.newsfeed.service;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import com.sparta.oneandzerobest.newsfeed.repository.NewsfeedRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NewsfeedService {

    private final NewsfeedRepository newsfeedRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public ResponseEntity<NewsfeedResponseDto> postContent(String token, NewsfeedRequestDto contentRequestDto) {
        try {
            // 토큰으로 유저 정보 가져오기
            User user = getUserFromToken(token);
            if (user == null) {
                throw new IllegalArgumentException("Invalid token");
            }
            Long userid = user.getId();

            Newsfeed newsfeed = Newsfeed.builder()
                    .userid(userid)
                    .content(contentRequestDto.getContent())
                    .build();
            newsfeedRepository.save(newsfeed);

            NewsfeedResponseDto newsfeedResponseDto = NewsfeedResponseDto.builder()
                    .id(newsfeed.getId())
                    .userId(newsfeed.getUserid())
                    .content(newsfeed.getContent())
                    .createdAt(newsfeed.getCreatedAt())
                    .build();

            return ResponseEntity.ok(newsfeedResponseDto);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    public ResponseEntity<Page<NewsfeedResponseDto>> getAllContents(int page, int size, boolean isASC, boolean like, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 정렬 ( 오름차순 / 내림차순)
            Sort.Direction direction = isASC ? Direction.ASC : Direction.DESC;

            // 좋아요 / 생성일
            Sort sort;
            if (!like) {
                sort = Sort.by(direction, "createdAt");
            } else {
                sort = Sort.by(direction, "likeCount");
            }

            Pageable pageable = PageRequest.of(page, size, sort);

            // 모든 게시글 조회
            Page<Newsfeed> newsfeedList;

            // 전체검색 / 기간별 검색
            if (startTime == null) {
                newsfeedList = newsfeedRepository.findAll(pageable);
            } else {
                newsfeedList = newsfeedRepository.findAllByCreateAtBetween(startTime, endTime, pageable);
            }

            Page<NewsfeedResponseDto> newsfeedResponseDtoPage = newsfeedList.map(
                    newsfeed -> NewsfeedResponseDto.builder()
                            .id(newsfeed.getId())
                            .userId(newsfeed.getUserid())
                            .content(newsfeed.getContent())
                            .createdAt(newsfeed.getCreatedAt())
                            .build());
            return ResponseEntity.ok(newsfeedResponseDtoPage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Transactional
    public ResponseEntity<NewsfeedResponseDto> putContent(String token, Long contentId, NewsfeedRequestDto contentRequestDto) {
        try {
            Newsfeed newsfeed = newsfeedRepository.findById(contentId)
                    .orElseThrow(() -> new RuntimeException("Content not found"));

            User user = getUserFromToken(token);
            // 유저가 없거나, 뉴스피드의 userid와 user의 id가 일치하지 않는다면
            if (user == null || !Objects.equals(newsfeed.getUserid(), user.getId())) {
                throw new IllegalArgumentException("Invalid token");
            }
            newsfeed.setContent(contentRequestDto.getContent());

            NewsfeedResponseDto newsfeedResponseDto = NewsfeedResponseDto.builder()
                    .id(newsfeed.getId())
                    .userId(newsfeed.getUserid())
                    .content(newsfeed.getContent())
                    .createdAt(newsfeed.getCreatedAt())
                    .build();

            return ResponseEntity.ok(newsfeedResponseDto);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    public ResponseEntity<Long> deleteContent(String token, Long contentId) {
        Newsfeed newsfeed = newsfeedRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        User user = getUserFromToken(token);
        // 유저가 없거나, 뉴스피드의 userid와 user의 id가 일치하지 않는다면
        if (user == null || !Objects.equals(user.getId(), newsfeed.getUserid())) {
            throw new IllegalArgumentException("Invalid token");
        }

        newsfeedRepository.delete(newsfeed);
        return ResponseEntity.ok(newsfeed.getId());
    }

    // 토큰으로 User 가져오기
    private User getUserFromToken(String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        return userRepository.findByUsername(username).orElse(null);
    }

    public String uploadImageToNewsfeed(String token, Long contentId, MultipartFile file) {
        // 이미지 업로드 로직
        return "imageUrl";
    }

    public String updateImageToNewsfeed(String token, Long contentId, MultipartFile file) {
        // 이미지 업데이트 로직
        return "updatedImageUrl";
    }
}