package com.sparta.oneandzerobest.newsfeed.service;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NewsfeedService {

    private final NewsfeedRepository newsfeedRepository;

    public ResponseEntity<NewsfeedResponseDto> postContent(NewsfeedRequestDto contentRequestDto) {

        try {
            Newsfeed newsfeed = new Newsfeed(contentRequestDto.getUserid(), contentRequestDto.getContent());
            newsfeedRepository.save(newsfeed);

            NewsfeedResponseDto newsfeedResponseDto = new NewsfeedResponseDto(newsfeed);

            return ResponseEntity.ok(newsfeedResponseDto);
        } catch (ConstraintViolationException e) {

            return ResponseEntity.badRequest().body(null);
        }

    }

    public ResponseEntity<Page<NewsfeedResponseDto>> getAllContents(int page, int size) {

        try {

            Sort.Direction direction = Direction.DESC;
            Sort sort = Sort.by(direction,"createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Newsfeed> newsfeedList = newsfeedRepository.findAll(pageable);
            Page<NewsfeedResponseDto> newsfeedResponseDtoPage = newsfeedList.map(NewsfeedResponseDto::new);
            return  ResponseEntity.ok(newsfeedResponseDtoPage);
        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().header("잘못된 요청입니다.").body(null);
        }

    }

    @Transactional
    public ResponseEntity<NewsfeedResponseDto> putContent(Long contentId,
        NewsfeedRequestDto contentRequestDto) {

        try {
            Newsfeed newsfeed = newsfeedRepository.findById(contentId).orElseThrow(() -> new RuntimeException("Content not found"));

            newsfeed.setContent(contentRequestDto.getContent());

        } catch (ConstraintViolationException e) {
            return ResponseEntity.badRequest().header("잘못된 요청입니다.").body(null);
        }

        return null;
    }


    public ResponseEntity<Long> deleteContent(Long contentId) {

        try{
            Newsfeed content = newsfeedRepository.findById(contentId).orElseThrow(() -> new RuntimeException("Content not found"));
            newsfeedRepository.delete(content);
            return ResponseEntity.ok(content.getId());
        }
        catch(RuntimeException e){
            return ResponseEntity.badRequest().header("잘못된 요청입니다.").body(null);
        }
    }
}
