package com.sparta.oneandzerobest.newsfeed.service;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import com.sparta.oneandzerobest.newsfeed.repository.NewsfeedRepository;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NewsfeedService {

    private final NewsfeedRepository newsfeedRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 게시글 작성
    public ResponseEntity<NewsfeedResponseDto> postContent(String token, NewsfeedRequestDto contentRequestDto) {

        try {
            // 토큰으로 유저 정보 가져오기
            User user = getUserFormToken(token);
            if(user == null) {
                throw new IllegalArgumentException("Invalid token");
            }
            Long userid = user.getId();

            Newsfeed newsfeed = new Newsfeed(userid, contentRequestDto.getContent());
            newsfeedRepository.save(newsfeed);

            NewsfeedResponseDto newsfeedResponseDto = new NewsfeedResponseDto(newsfeed);

            return ResponseEntity.ok(newsfeedResponseDto);
        } catch (ConstraintViolationException e) {

            return ResponseEntity.badRequest().body(null);
        }

    }

    // 게시글 조회
    public ResponseEntity<Page<NewsfeedResponseDto>> getAllContents(int page, int size,
        LocalDateTime startTime, LocalDateTime endTime) {

        try {

            // 생성일 기준으로 최신순 정렬
            Sort.Direction direction = Direction.DESC;
            Sort sort = Sort.by(direction,"createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);

            // 모든 게시글 조회
            Page<Newsfeed> newsfeedList;

            if(startTime == null){
                newsfeedList = newsfeedRepository.findAll(pageable);
            }
            else{
                newsfeedList = newsfeedRepository.findAllByCreateAtBetween(startTime,endTime,pageable);
            }

            Page<NewsfeedResponseDto> newsfeedResponseDtoPage = newsfeedList.map(NewsfeedResponseDto::new);
            return  ResponseEntity.ok(newsfeedResponseDtoPage);
        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(null);
        }

    }

    // 게시글 수정
    @Transactional
    public ResponseEntity<NewsfeedResponseDto> putContent(String token, Long contentId,
        NewsfeedRequestDto contentRequestDto) {

        try {
            User user = getUserFormToken(token);
            if(user == null) {
                throw new IllegalArgumentException("Invalid token");
            }

            Newsfeed newsfeed = newsfeedRepository.findById(contentId).orElseThrow(() -> new RuntimeException("Content not found"));
            newsfeed.setContent(contentRequestDto.getContent());

        } catch (ConstraintViolationException e) {
            return ResponseEntity.badRequest().body(null);
        }

        return null;
    }


    // 게시글 삭제
    public ResponseEntity<Long> deleteContent(String token, Long contentId) {

        try{
            User user = getUserFormToken(token);
            if(user == null) {
                throw new IllegalArgumentException("Invalid token");
            }

            Newsfeed content = newsfeedRepository.findById(contentId).orElseThrow(() -> new RuntimeException("Content not found"));
            newsfeedRepository.delete(content);
            return ResponseEntity.ok(content.getId());
        }
        catch(RuntimeException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 토큰으로 User 가져오기
    private User getUserFormToken(String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        return userRepository.findByUsername(username).orElse(null);
    }
}
